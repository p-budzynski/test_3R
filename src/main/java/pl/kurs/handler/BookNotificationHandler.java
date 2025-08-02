package pl.kurs.handler;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import pl.kurs.entity.Book;
import pl.kurs.entity.SubscriptionType;
import pl.kurs.event.BookCreatedEvent;
import pl.kurs.messaging.producer.EmailProducer;
import pl.kurs.service.BookService;
import pl.kurs.service.SubscriptionService;

import java.util.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class BookNotificationHandler {

    private final BookService bookService;
    private final SubscriptionService subscriptionService;
    private final EmailProducer emailProducer;

    @EventListener
    @Async("bookNotificationExecutor")
    @Transactional
    public void handleBookCreated(BookCreatedEvent event) {
        log.info("Processing notifications for book ID: {}", event.getBookId());

        Optional<Book> bookOpt = bookService.findById(event.getBookId());
        if (bookOpt.isEmpty()) {
            log.warn("Book not found with ID: {}", event.getBookId());
            return;
        }

        Book book = bookOpt.get();
        sendNotificationsForNewBook(book);
    }

    private void sendNotificationsForNewBook(Book book) {
        try {
            List<String> categoryEmails = subscriptionService
                    .findEmailsBySubscriptionTypeAndValue(SubscriptionType.CATEGORY, book.getCategory().getName());

            List<String> authorEmails = subscriptionService
                    .findEmailsBySubscriptionTypeAndValue(SubscriptionType.AUTHOR, book.getAuthor());

            Set<String> allEmails = new HashSet<>();
            allEmails.addAll(categoryEmails);
            allEmails.addAll(authorEmails);

            if (!allEmails.isEmpty()) {
                emailProducer.sendNewBookNotifications(
                        new ArrayList<>(allEmails),
                        book.getTitle(),
                        book.getAuthor(),
                        book.getCategory().getName()
                );
                log.info("Queued notifications for {} subscribers for new book: {}",
                        allEmails.size(), book.getTitle());
            } else {
                log.info("No subscribers found for book: {}", book.getTitle());
            }
        } catch (Exception e) {
            log.error("Error sending notifications for book: {} - {}", book.getTitle(), e.getMessage(), e);
        }
    }
}