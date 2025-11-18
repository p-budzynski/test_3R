package pl.kurs.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import pl.kurs.service.SubscriptionNotificationService;

@Component
@RequiredArgsConstructor
@Slf4j
public class DailyNotificationScheduler {
    private final SubscriptionNotificationService notificationService;

    @Scheduled(cron = "${app.scheduling.daily-notifications}")
    public void runDailyNotificationJob() {
        try {
            notificationService.processAllNotificationsStream();
            log.info("Daily notification job completed successfully");
        } catch (Exception ex) {
            log.error("Daily notification job failed", ex);
        }
    }

    @Scheduled(cron = "${app.scheduling.daily-delete-notifications}")
    public void runDailyDeleteNotificationJob() {
        try {
            notificationService.deleteAllNotificationProcessed();
            log.info("Daily delete notification processed job completed successfully");
        } catch (Exception ex) {
            log.error("Daily delete notification job failed", ex);
        }
    }
}
