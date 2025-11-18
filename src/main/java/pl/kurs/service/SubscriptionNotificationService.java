package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.entity.Book;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.entity.SubscriptionNotification;
import pl.kurs.repository.SubscriptionNotificationRepository;

import java.util.*;
import java.util.stream.Stream;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionNotificationService {
    private final SubscriptionNotificationRepository notificationRepository;
    private final SubscriptionService subscriptionService;
    private final BookService bookService;
    private final MailService mailService;

    @Transactional
    public void createNotificationsForSubscriptions(Long bookId) {
        Book book = bookService.findBookById(bookId);

        List<Subscription> subscriptions = subscriptionService.
                findByAuthorIdOrCategoryId(book.getAuthor().getId(), book.getCategory().getId());

        if (subscriptions.isEmpty()) {
            return;
        }

        List<SubscriptionNotification> notifications = subscriptions.stream()
                .map(subscription -> SubscriptionNotification.builder()
                        .client(subscription.getClient())
                        .book(book)
                        .processed(false)
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void processAllNotificationsStream() {
        Map<Long, List<SubscriptionNotification>> grouped = new HashMap<>();

        try (Stream<SubscriptionNotification> stream = notificationRepository.streamUnprocessed()) {
            stream.forEach(sn -> {
                Long clientId = sn.getClient().getId();
                grouped
                        .computeIfAbsent(clientId, c -> new ArrayList<>())
                        .add(sn);
            });
        }

        grouped.forEach(((clientId, subscriptionNotifications) -> {
            Client client = subscriptionNotifications.get(0).getClient();
            try {
                List<Book> books = subscriptionNotifications.stream()
                        .map(SubscriptionNotification::getBook)
                        .distinct()
                        .toList();

                mailService.sendNewBookNotifications(client, books);

                List<Long> ids = subscriptionNotifications.stream()
                        .map(SubscriptionNotification::getId)
                        .toList();

                processBatch(ids);

            } catch (Exception ex) {
                log.error("Failed to send notification {}", client.getEmail(), ex);
            }
        }));
    }

    @Transactional
    public void deleteAllNotificationProcessed() {
        int batchSize = 1000;
        List<Long> ids;

        do {
            ids = notificationRepository.findProcessedIds(PageRequest.of(0, batchSize));

            if (!ids.isEmpty()) {
                notificationRepository.deleteAllByIdInBatch(ids);
                log.info("Deleted {} processed notifications", ids.size());
            }
        } while (!ids.isEmpty());

        log.info("All processed notifications deleted.");
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processBatch(List<Long> ids) {
        notificationRepository.markProcessed(ids);
        log.info("Batch marked as processed: {}", ids.size());
    }

}