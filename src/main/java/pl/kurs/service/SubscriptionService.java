package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.entity.SubscriptionType;
import pl.kurs.mapper.SubscriptionMapper;
import pl.kurs.repository.SubscriptionRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final ClientService clientService;
    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionDto createSubscription(SubscriptionDto dto) {
        Client client = clientService.getVerifiedClientById(dto.getClientId());

        SubscriptionType subscriptionType = SubscriptionType.fromString(dto.getSubscriptionType());

        Subscription subscription = subscriptionMapper.dtoToEntity(dto);
        subscription.setClient(client);
        subscription.setSubscriptionType(subscriptionType);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.entityToDto(savedSubscription);
    }

    public void cancelSubscriptionById(Long id) {
        subscriptionRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public List<String> findEmailsBySubscriptionTypeAndValue(SubscriptionType subscriptionType, String name) {
        return subscriptionRepository.findEmailsBySubscriptionTypeAndSubscriptionValue(subscriptionType, name);
    }

}
