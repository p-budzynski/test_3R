package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.entity.Book;
import pl.kurs.entity.Subscription;
import pl.kurs.entity.SubscriptionNotification;
import pl.kurs.repository.SubscriptionNotificationRepository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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

    public void processAllNotificationsStream() {
       List<Long> ids = new ArrayList<>();
       int batchSize = 1000;

       try (Stream<SubscriptionNotification> stream =
               notificationRepository.streamUnprocessed()) {

           stream.forEach(sn -> {
               try {
                   mailService.sendNewBookNotifications(
                           sn.getClient(),
                           Collections.singletonList(sn.getBook())
                   );
                   ids.add(sn.getId());

                   if (ids.size() >= batchSize) {
                       processBatch(ids);
                       ids.clear();
                   }

               } catch (Exception ex) {
                   log.error("Failed to send notification {}", sn.getId(), ex);
               }
           });
       }

       if (!ids.isEmpty()) {
           processBatch(ids);
       }

       notificationRepository.deleteByProcessed();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processBatch(List<Long> ids) {
        notificationRepository.markProcessed(ids);
        log.info("Batch marked as processed: {}", ids.size());
    }

}