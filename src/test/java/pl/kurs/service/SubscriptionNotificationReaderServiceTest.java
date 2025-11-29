package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.entity.*;
import pl.kurs.repository.SubscriptionNotificationRepository;

import java.util.List;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class SubscriptionNotificationReaderServiceTest {

    @Mock
    private SubscriptionNotificationRepository notificationRepositoryMock;

    @Mock
    private SubscriptionNotificationProcessingService processingServiceMock;

    @Mock
    private SubscriptionService subscriptionServiceMock;

    @Mock
    private BookService bookServiceMock;

    @InjectMocks
    private SubscriptionNotificationReaderService readerService;

    @Test
    void shouldCreateNotificationsForSubscriptions() {
        //given
        Long bookId = 1L;
        Book book = new Book();
        Author author = new Author();
        Category category = new Category();
        Client client = new Client();

        author.setId(1L);
        category.setId(1L);
        book.setAuthor(author);
        book.setCategory(category);

        Subscription subscription = new Subscription();
        subscription.setClient(client);

        when(bookServiceMock.findBookById(bookId)).thenReturn(book);
        when(subscriptionServiceMock.findByAuthorIdOrCategoryId(1L, 1L))
                .thenReturn(List.of(subscription));

        ArgumentCaptor<List<SubscriptionNotification>> captor = ArgumentCaptor.forClass(List.class);

        //when
        readerService.createNotificationsForSubscriptions(bookId);

        //then
        verify(notificationRepositoryMock).saveAll(captor.capture());

        List<SubscriptionNotification> saved = captor.getValue();
        assertThat(saved.get(0).getClient()).isEqualTo(client);
        assertThat(saved.get(0).getBook()).isEqualTo(book);
    }

    @Test
    void processAllNotificationsStream() {
        //given
        Long bookId = 1L;
        Book book = new Book();
        Author author = new Author();
        Category category = new Category();

        author.setId(1L);
        category.setId(2L);
        book.setAuthor(author);
        book.setCategory(category);

        when(bookServiceMock.findBookById(bookId)).thenReturn(book);
        when(subscriptionServiceMock.findByAuthorIdOrCategoryId(1L, 2L)).thenReturn(List.of());

        //when
        readerService.createNotificationsForSubscriptions(bookId);

        //then
        verify(notificationRepositoryMock, never()).saveAll(any());
    }

    @Test
    void shouldProcessNotificationGroupedByClient() {
        //given
        Client client1 = new Client();
        client1.setId(1L);

        Client client2 = new Client();
        client2.setId(2L);

        SubscriptionNotification notification1 = new SubscriptionNotification(1L, client1, new Book());
        SubscriptionNotification notification2 = new SubscriptionNotification(2L, client1, new Book());
        SubscriptionNotification notification3 = new SubscriptionNotification(3L, client2, new Book());

        List<SubscriptionNotification> notifications = List.of(notification1, notification2, notification3);

        when(notificationRepositoryMock.streamSubscriptionNotification())
                .thenAnswer(invocation -> notifications.stream());

        //when
        readerService.processAllNotificationsStream();

        //then
        verify(processingServiceMock).processBucket(
                argThat(client -> client.getId().equals(1L)),
                argThat(list -> list.size() == 2));

        verify(processingServiceMock).processBucket(
                argThat(client -> client.getId().equals(2L)),
                argThat(list -> list.size() == 1));
    }

    @Test
    void shouldHandleEmptyStream() {
        //given
        when(notificationRepositoryMock.streamSubscriptionNotification())
                .thenAnswer(invocation -> Stream.empty());

        //when
        readerService.processAllNotificationsStream();

        //then
        verify(processingServiceMock, never()).processBucket(any(), any());
    }
}