package pl.kurs.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.event.BookCreatedEvent;
import pl.kurs.service.SubscriptionNotificationReaderService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class BookCreatedEventListenerTest {

    @Mock
    private SubscriptionNotificationReaderService notificationReaderServiceMock;

    @InjectMocks
    private BookCreatedEventListener eventListener;

    @Test
    void shouldCallNotificationServiceOnBookCreatedEvent() {
        //given
        Long bookId = 1L;
        BookCreatedEvent event = new BookCreatedEvent(this, bookId);

        //when
        eventListener.handleBookCreated(event);

        //then
        verify(notificationReaderServiceMock).createNotificationsForSubscriptions(bookId);
    }

}