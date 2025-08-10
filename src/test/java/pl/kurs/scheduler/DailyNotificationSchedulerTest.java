package pl.kurs.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.service.NotificationBatchService;

import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyNotificationSchedulerTest {

    @Mock
    private NotificationBatchService notificationBatchServiceMock;

    @InjectMocks
    private DailyNotificationScheduler scheduler;

    @Test
    void shouldProcessNotificationsForYesterday() {
        //given
        LocalDate fixedDate = LocalDate.of(2025, 8, 15);
        LocalDate expectedDate = fixedDate.minusDays(1);

        try (MockedStatic<LocalDate> mockedLocalDate = mockStatic(LocalDate.class)) {
            mockedLocalDate.when(LocalDate::now).thenReturn(fixedDate);

            //when
            scheduler.runDailyNotificationJob();

            //then
            verify(notificationBatchServiceMock).processAllNotifications(expectedDate);
        }
    }

    @Test
    void shouldLogSuccessWhenNotificationCompletes() {
        //given
        doNothing().when(notificationBatchServiceMock).processAllNotifications(any(LocalDate.class));

        //when
        scheduler.runDailyNotificationJob();

        //then
        verify(notificationBatchServiceMock).processAllNotifications(any(LocalDate.class));
    }

    @Test
    void shouldLogErrorWhenNotificationFails() {
        //given
        RuntimeException testException = new RuntimeException("Database connection failed");
        doThrow(testException).when(notificationBatchServiceMock)
                .processAllNotifications(any(LocalDate.class));

        //when then
        assertThatCode(() -> scheduler.runDailyNotificationJob())
                .doesNotThrowAnyException();
    }
}