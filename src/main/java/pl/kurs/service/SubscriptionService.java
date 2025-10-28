package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.mapper.SubscriptionMapper;
import pl.kurs.repository.SubscriptionRepository;

import java.time.LocalDate;
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

        Subscription subscription = subscriptionMapper.dtoToEntity(dto);
        subscription.setClient(client);

        Subscription savedSubscription = subscriptionRepository.save(subscription);

        return subscriptionMapper.entityToDto(savedSubscription);
    }

    public void cancelSubscriptionById(Long id) {
        subscriptionRepository.deleteById(id);
    }


    public List<Subscription> findByAuthorIdOrCategoryId(Long authorId, Long categoryId) {
        return subscriptionRepository.findByAuthorIdOrCategoryId(authorId, categoryId);
    }
}
