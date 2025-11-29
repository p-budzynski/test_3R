package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.entity.Book;
import pl.kurs.entity.Client;
import pl.kurs.entity.SubscriptionNotification;
import pl.kurs.repository.SubscriptionNotificationRepository;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionNotificationProcessingServiceTest {

    @Mock
    private SubscriptionNotificationRepository notificationRepositoryMock;

    @Mock
    private MailService mailServiceMock;

    @InjectMocks
    private SubscriptionNotificationProcessingService processingService;

    @Test
    void shouldProcessBucketAndDeleteNotifications() {
        //given
        Client client = new Client();
        client.setId(123L);

        Book book1 = new Book();
        book1.setId(1L);
        Book book2 = new Book();
        book2.setId(2L);

        SubscriptionNotification notification1 = new SubscriptionNotification(1L, client, book1);
        SubscriptionNotification notification2 = new SubscriptionNotification(2L, client, book2);

        List<SubscriptionNotification> bucket = List.of(notification1, notification2);

        //when
        processingService.processBucket(client, bucket);

        //then
        verify(mailServiceMock).sendNewBookNotifications(
                eq(client),
                argThat(list -> list.contains(book1) && list.contains(book2))
        );

        verify(notificationRepositoryMock).deleteAllByIdInBatch(eq(List.of(1L, 2L)));
    }

    @Test
    void shouldNotDeleteNotificationsWhenMailServiceFails() {
        //given
        Client client = new Client(1L, "John", "Rambo", "j.rambo@mail.com", null, true, null, null);
        Book book = new Book();
        book.setId(1L);

        SubscriptionNotification notification = new SubscriptionNotification(1L, client, book);
        List<SubscriptionNotification> bucket = List.of(notification);

        doThrow(new RuntimeException("TEST - Mail send failure"))
                .when(mailServiceMock)
                .sendNewBookNotifications(any(), any());

        //when
        processingService.processBucket(client, bucket);

        //then
        verify(notificationRepositoryMock, never())
                .deleteAllByIdInBatch(any());
    }
}