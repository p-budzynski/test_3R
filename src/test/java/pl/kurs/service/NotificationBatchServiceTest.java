package pl.kurs.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import pl.kurs.config.NotificationProperties;
import pl.kurs.messaging.producer.EmailProducer;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationBatchServiceTest {

    @Mock
    private SubscriptionService subscriptionServiceMock;

    @Mock
    private EmailProducer emailProducerMock;

    @Mock
    private NotificationProperties notificationPropertiesMock;

    @InjectMocks
    private NotificationBatchService notificationBatchService;

    private final LocalDate testDate = LocalDate.of(2025, 8, 15);
    private final int batchSize = 10;

    @BeforeEach
    void setUp() {
        when(notificationPropertiesMock.getBatchSize()).thenReturn(batchSize);
    }

    @Test
    void shouldProcessBatchSuccessfully() throws ExecutionException, InterruptedException {
        //given
        Object[] row1 = {"user1@example.com", "Book 1, Book 2"};
        Object[] row2 = {"user2@example.com", "Book 3, Book 4"};
        List<Object[]> content = Arrays.asList(row1, row2);

        Slice<Object[]> batch = new SliceImpl<>(content, PageRequest.of(0, batchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize)))
                .thenReturn(batch);

        //when
        CompletableFuture<Integer> result = notificationBatchService.processBatch(testDate, 0);

        //then
        assertThat(result.get()).isEqualTo(2);

        verify(emailProducerMock).sendDailyBookList(
                eq("user1@example.com"),
                eq("New books in the library - 2025-08-15"),
                eq("Here are the new books added to the library today, matching your subscriptions:\n\nBook 1, Book 2")
        );

        verify(emailProducerMock).sendDailyBookList(
                eq("user2@example.com"),
                eq("New books in the library - 2025-08-15"),
                eq("Here are the new books added to the library today, matching your subscriptions:\n\nBook 3, Book 4")
        );
    }

    @Test
    void shouldProcessEmptyBatch() throws ExecutionException, InterruptedException {
        //given
        Slice<Object[]> emptyBatch = new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, batchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize)))
                .thenReturn(emptyBatch);

        //when
        CompletableFuture<Integer> result = notificationBatchService.processBatch(testDate, 0);

        //then
        assertThat(result.get()).isEqualTo(0);
        verify(emailProducerMock, never()).sendDailyBookList(anyString(), anyString(), anyString());
    }

    @Test
    void shouldHandleEmailProducerException() throws ExecutionException, InterruptedException {
        //given
        Object[] row1 = {"user1@example.com", "Book 1"};
        Object[] row2 = {"user2@example.com", "Book 2"};
        List<Object[]> content = Arrays.asList(row1, row2);

        Slice<Object[]> batch = new SliceImpl<>(content, PageRequest.of(0, batchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize)))
                .thenReturn(batch);

        doThrow(new RuntimeException("Email sending failed"))
                .when(emailProducerMock).sendDailyBookList(eq("user1@example.com"), anyString(), anyString());

        doNothing()
                .when(emailProducerMock).sendDailyBookList(eq("user2@example.com"), anyString(), anyString());

        //when
        CompletableFuture<Integer> result = notificationBatchService.processBatch(testDate, 0);

        //then
        assertThat(result.get()).isEqualTo(1);

        verify(emailProducerMock).sendDailyBookList(eq("user1@example.com"), anyString(), anyString());
        verify(emailProducerMock).sendDailyBookList(eq("user2@example.com"), anyString(), anyString());
    }

    @Test
    void shouldProcessAllNotificationsWithSingleBatch() {
        //given
        Object[] row1 = {"user1@example.com", "Book 1"};
        List<Object[]> content = Collections.singletonList(row1);

        Slice<Object[]> batch = new SliceImpl<>(content, PageRequest.of(0, batchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(eq(testDate), any(PageRequest.class)))
                .thenReturn(batch);

        //when
        assertThatCode(() -> notificationBatchService.processAllNotifications(testDate))
                .doesNotThrowAnyException();

        //then
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize));
    }

    @Test
    void shouldProcessAllNotificationsWithMultipleBatches() {
        //given
        List<Object[]> firstBatchContent = Arrays.asList(
                new Object[]{"user1@example.com", "Book 1"},
                new Object[]{"user2@example.com", "Book 2"},
                new Object[]{"user3@example.com", "Book 3"},
                new Object[]{"user4@example.com", "Book 4"},
                new Object[]{"user5@example.com", "Book 5"},
                new Object[]{"user6@example.com", "Book 6"},
                new Object[]{"user7@example.com", "Book 7"},
                new Object[]{"user8@example.com", "Book 8"},
                new Object[]{"user9@example.com", "Book 9"},
                new Object[]{"user10@example.com", "Book 10"}
        );

        List<Object[]> secondBatchContent = Arrays.asList(
                new Object[]{"user11@example.com", "Book 11"},
                new Object[]{"user12@example.com", "Book 12"},
                new Object[]{"user13@example.com", "Book 13"},
                new Object[]{"user14@example.com", "Book 14"},
                new Object[]{"user15@example.com", "Book 15"}
        );

        Slice<Object[]> firstBatch = new SliceImpl<>(firstBatchContent, PageRequest.of(0, batchSize), true);
        Slice<Object[]> secondBatch = new SliceImpl<>(secondBatchContent, PageRequest.of(1, batchSize), false);

        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize)))
                .thenReturn(firstBatch);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(1, batchSize)))
                .thenReturn(secondBatch);

        //when
        assertThatCode(() -> notificationBatchService.processAllNotifications(testDate))
                .doesNotThrowAnyException();

        //then
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize));
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(1, batchSize));
        verify(subscriptionServiceMock, times(2)).findEmailsAndBooksForDatePaginated(eq(testDate), any(PageRequest.class));
    }

    @Test
    void shouldStopProcessingWhenBatchThrowsException() {
        //given
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(eq(testDate), any(PageRequest.class)))
                .thenThrow(new RuntimeException("Database connection failed"));

        //when
        assertThatCode(() -> notificationBatchService.processAllNotifications(testDate))
                .doesNotThrowAnyException();

        //then
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize));
        verify(subscriptionServiceMock, times(1)).findEmailsAndBooksForDatePaginated(eq(testDate), any(PageRequest.class));
    }

    @Test
    void shouldUseCorrectPageRequestParameters() throws ExecutionException, InterruptedException {
        //given
        int testPage = 5;
        Slice<Object[]> emptyBatch = new SliceImpl<>(Collections.emptyList(), PageRequest.of(testPage, batchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(testPage, batchSize)))
                .thenReturn(emptyBatch);

        //when
        CompletableFuture<Integer> result = notificationBatchService.processBatch(testDate, testPage);

        //then
        assertThat(result.get()).isEqualTo(0);
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(
                testDate,
                PageRequest.of(testPage, batchSize)
        );
    }

    @Test
    void shouldCreateCorrectEmailContent() {
        //given
        Object[] row = {"test@example.com", "Clean Code, Design Patterns"};
        List<Object[]> content = Collections.singletonList(row);

        Slice<Object[]> batch = new SliceImpl<>(content, PageRequest.of(0, batchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize)))
                .thenReturn(batch);

        //when
        notificationBatchService.processBatch(testDate, 0);

        //then
        verify(emailProducerMock).sendDailyBookList(
                eq("test@example.com"),
                eq("New books in the library - 2025-08-15"),
                eq("Here are the new books added to the library today, matching your subscriptions:\n\nClean Code, Design Patterns")
        );
    }

    @Test
    void shouldContinueProcessingAfterSingleEmailFailure() throws ExecutionException, InterruptedException {
        //given
        Object[] row1 = {"failing@example.com", "Book 1"};
        Object[] row2 = {"success@example.com", "Book 2"};
        List<Object[]> content = Arrays.asList(row1, row2);

        Slice<Object[]> batch = new SliceImpl<>(content, PageRequest.of(0, batchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize)))
                .thenReturn(batch);

        doThrow(new RuntimeException("Email failed"))
                .when(emailProducerMock).sendDailyBookList(eq("failing@example.com"), anyString(), anyString());

        doNothing()
                .when(emailProducerMock).sendDailyBookList(eq("success@example.com"), anyString(), anyString());

        //when
        CompletableFuture<Integer> result = notificationBatchService.processBatch(testDate, 0);

        //then
        assertThat(result.get()).isEqualTo(1);
        verify(emailProducerMock).sendDailyBookList(eq("failing@example.com"), anyString(), anyString());
        verify(emailProducerMock).sendDailyBookList(eq("success@example.com"), anyString(), anyString());
    }

    @Test
    void shouldStopWhenBatchSizeIsNotFull() {
        //given
        Object[] row = {"user@example.com", "Book 1"};
        List<Object[]> content = Collections.singletonList(row);

        Slice<Object[]> smallBatch = new SliceImpl<>(content, PageRequest.of(0, batchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(eq(testDate), any(PageRequest.class)))
                .thenReturn(smallBatch);

        //when
        assertThatCode(() -> notificationBatchService.processAllNotifications(testDate))
                .doesNotThrowAnyException();

        //then
        verify(subscriptionServiceMock, times(1)).findEmailsAndBooksForDatePaginated(eq(testDate), any(PageRequest.class));
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize));
    }

    @Test
    void shouldProcessMultiplePagesUntilSmallBatch() {
        //given
        List<Object[]> fullBatch = Collections.nCopies(batchSize, new Object[]{"user@example.com", "Book"});
        Slice<Object[]> firstBatch = new SliceImpl<>(fullBatch, PageRequest.of(0, batchSize), true);

        List<Object[]> partialBatch = Collections.singletonList(new Object[]{"user@example.com", "Book"});
        Slice<Object[]> secondBatch = new SliceImpl<>(partialBatch, PageRequest.of(1, batchSize), false);

        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize)))
                .thenReturn(firstBatch);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(1, batchSize)))
                .thenReturn(secondBatch);

        //when
        assertThatCode(() -> notificationBatchService.processAllNotifications(testDate))
                .doesNotThrowAnyException();

        //then
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, batchSize));
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(1, batchSize));
        verify(subscriptionServiceMock, times(2)).findEmailsAndBooksForDatePaginated(eq(testDate), any(PageRequest.class));
    }

    @Test
    void shouldUseCorrectBatchSizeFromProperties() throws ExecutionException, InterruptedException {
        //given
        int customBatchSize = 5;
        when(notificationPropertiesMock.getBatchSize()).thenReturn(customBatchSize);

        Slice<Object[]> batch = new SliceImpl<>(Collections.emptyList(), PageRequest.of(0, customBatchSize), false);
        when(subscriptionServiceMock.findEmailsAndBooksForDatePaginated(testDate, PageRequest.of(0, customBatchSize)))
                .thenReturn(batch);

        //when
        notificationBatchService.processBatch(testDate, 0);

        //then
        verify(subscriptionServiceMock).findEmailsAndBooksForDatePaginated(
                testDate,
                PageRequest.of(0, customBatchSize)
        );
    }
}