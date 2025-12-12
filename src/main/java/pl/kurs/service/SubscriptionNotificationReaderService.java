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
public class SubscriptionNotificationReaderService {
    private final SubscriptionNotificationRepository notificationRepository;
    private final SubscriptionNotificationProcessingService processingService;
    private final SubscriptionService subscriptionService;
    private final BookService bookService;

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
                    processingService.processBucket(currentClient, new ArrayList<>(bucket));
                    bucket.clear();
                }

                currentClientId = clientId;
                currentClient = sn.getClient();
                bucket.add(sn);
            }
        }

        if (!bucket.isEmpty()) {
            processingService.processBucket(currentClient, new ArrayList<>(bucket));
        }
    }

}