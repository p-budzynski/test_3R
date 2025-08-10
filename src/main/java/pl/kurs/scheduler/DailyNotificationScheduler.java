package pl.kurs.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.kurs.service.NotificationBatchService;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyNotificationScheduler {

    private final NotificationBatchService notificationBatchService;

    @Scheduled(cron = "${app.scheduling.daily-notifications:0 0 0 * * *}")
    public void runDailyNotificationJob() {
        try {
            LocalDate yesterday = LocalDate.now().minusDays(1);
            notificationBatchService.processAllNotifications(yesterday);

            log.info("Daily notification job completed successfully");
        } catch (Exception e) {
            log.error("Daily notification job failed", e);
        }
    }
}
