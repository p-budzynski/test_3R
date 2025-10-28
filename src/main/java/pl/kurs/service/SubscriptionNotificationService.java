package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.entity.Book;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.entity.SubscriptionNotification;
import pl.kurs.repository.SubscriptionNotificationRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SubscriptionNotificationService {
    private final SubscriptionNotificationRepository notificationRepository;
    private final SubscriptionService subscriptionService;
    private final BookService bookService;
    private final MailService mailService;

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
    public void processAllNotifications() {
        List<Client> clients = notificationRepository.findClientsWithNotifications();

        for (Client client : clients) {
            List<Book> books = notificationRepository.findBooksByClientId(client.getId());
            mailService.sendNewBookNotifications(client, books);
        }

        notificationRepository.deleteAll();
    }
}
