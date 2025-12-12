package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.entity.Book;
import pl.kurs.entity.Client;
import pl.kurs.entity.SubscriptionNotification;
import pl.kurs.repository.SubscriptionNotificationRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionNotificationProcessingService {
    private final SubscriptionNotificationRepository notificationRepository;
    private final MailService mailService;

    @Async("notificationsExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processBucket(Client client, List<SubscriptionNotification> bucket) {
        try {
            List<Book> books = bucket.stream()
                    .map(SubscriptionNotification::getBook)
                    .distinct()
                    .toList();

            mailService.sendNewBookNotifications(client, books);

            List<Long> ids = bucket.stream()
                    .map(SubscriptionNotification::getId)
                    .toList();

            notificationRepository.deleteAllByIdInBatch(ids);

            log.info("Processed and deleted {} notifications for client {}", ids.size(), client.getId());
        } catch (Exception ex) {
            log.error("Failed to send notification to {}", client.getEmail(), ex);
        }
    }

}
