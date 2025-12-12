package pl.kurs.scheduler;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.service.SubscriptionNotificationReaderService;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DailyNotificationSchedulerTest {

    @Mock
    private SubscriptionNotificationReaderService readerServiceMock;

    @InjectMocks
    private DailyNotificationScheduler scheduler;

    @Test
    void shouldCallNotificationServiceWhenJobRuns() {
        //when
        scheduler.runDailyNotificationJob();

        //then
        verify(readerServiceMock).processAllNotificationsStream();
    }

    @Test
    void shouldNotThrowExceptionWhenNotificationServiceFails() {
        //when
        doThrow(new RuntimeException("TEST - Daily notification job failed"))
                .when(readerServiceMock).processAllNotificationsStream();

        scheduler.runDailyNotificationJob();

        //then
        verify(readerServiceMock).processAllNotificationsStream();
    }

}