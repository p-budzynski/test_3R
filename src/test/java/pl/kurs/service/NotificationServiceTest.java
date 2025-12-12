package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.kurs.entity.Book;
import pl.kurs.event.BookCreatedEvent;
import pl.kurs.event.ClientVerificationEvent;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class NotificationServiceTest {

    @Mock
    private ApplicationEventPublisher eventPublisherMock;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void shouldPublishBookCreatedEvent() {
        //given
        Book book = new Book();
        book.setId(10L);

        ArgumentCaptor<BookCreatedEvent> captor = ArgumentCaptor.forClass(BookCreatedEvent.class);

        //when
        notificationService.publishCreatedBookNotification(book);

        //then
        verify(eventPublisherMock).publishEvent(captor.capture());

        BookCreatedEvent bookEvent = captor.getValue();
        assertThat(bookEvent.getBookId()).isEqualTo(book.getId());
        assertThat(bookEvent.getSource()).isEqualTo(notificationService);
    }

    @Test
    void shouldPublishClientVerificationEvent() {
        //given
        String email = "test@email.com";
        String token = "test1234";

        ArgumentCaptor<ClientVerificationEvent> captor = ArgumentCaptor.forClass(ClientVerificationEvent.class);

        //when
        notificationService.publishClientRegistryNotification(email, token);

        //then
        verify(eventPublisherMock).publishEvent(captor.capture());

        ClientVerificationEvent clientEvent = captor.getValue();
        assertThat(clientEvent.getMail()).isEqualTo(email);
        assertThat(clientEvent.getToken()).isEqualTo(token);
        assertThat(clientEvent.getSource()).isEqualTo(notificationService);
    }

}