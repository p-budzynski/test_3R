package pl.kurs.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.kurs.event.BookCreatedEvent;
import pl.kurs.service.SubscriptionNotificationReaderService;

@Component
@RequiredArgsConstructor
public class BookCreatedEventListener {

    private final SubscriptionNotificationReaderService notificationService;

    @EventListener
    public void handleBookCreated(BookCreatedEvent event) {
        notificationService.createNotificationsForSubscriptions(event.getBookId());
    }

}
