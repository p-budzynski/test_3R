package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.dto.ClientDto;
import pl.kurs.entity.Client;
import pl.kurs.exception.EmailNotVerifiedException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.mapper.ClientMapper;
import pl.kurs.messaging.producer.EmailProducer;
import pl.kurs.repository.ClientRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final EmailProducer emailProducer;
    private final ClientMapper clientMapper;

    @Transactional
    public ClientDto registerClient(ClientDto dto) {
        String verificationToken = UUID.randomUUID().toString();

        Client client = clientMapper.dtoToEntity(dto);
        client.setVerificationToken(verificationToken);

        Client savedClient = clientRepository.save(client);
        emailProducer.sendVerificationEmail(dto.getEmail(), verificationToken);

        return clientMapper.entityToDto(savedClient);
    }

    public boolean verifyEmail(String token) {
        return clientRepository.findByVerificationToken(token)
                .map(client -> {
                    client.setEmailVerified(true);
                    client.setVerificationToken(null);
                    clientRepository.save(client);
                    return true;
                })
                .orElse(false);
    }

    @Transactional(readOnly = true)
    public Client getClientById(Long id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Client not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Client getVerifiedClientById(Long id) {
        Client client = getClientById(id);
        if (!client.getEmailVerified()) {
            throw new EmailNotVerifiedException("Email must be verified before creating subscription");
        }
        return client;
    }

}
