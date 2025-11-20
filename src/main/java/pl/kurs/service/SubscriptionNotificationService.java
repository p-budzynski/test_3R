package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
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
                        .build())
                .toList();

        notificationRepository.saveAll(notifications);
    }

    @Transactional
    public void processAllNotificationsStream() {
        Long currentClientId = null;
        Client currentClient = null;
        List<SubscriptionNotification> bucket = new ArrayList<>();

        try (Stream<SubscriptionNotification> stream = notificationRepository.streamSubscriptionNotification()) {
            Iterator<SubscriptionNotification> it = stream.iterator();

            while (it.hasNext()) {
                SubscriptionNotification sn = it.next();
                Long clientId = sn.getClient().getId();

                if (currentClientId != null && !currentClientId.equals(clientId)) {
                    processBucket(currentClient, bucket);
                    bucket.clear();
                }

                currentClientId = clientId;
                currentClient = sn.getClient();
                bucket.add(sn);
            }
        }

        if (!bucket.isEmpty()) {
            processBucket(currentClient, bucket);
        }
    }

    @Transactional
    private void processBucket(Client client, List<SubscriptionNotification> bucket) {
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