package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.kurs.scheduler.DailyNotificationScheduler;

@RestController
@RequestMapping("/job")
@RequiredArgsConstructor
public class JobController {
    private final DailyNotificationScheduler notificationScheduler;

    @PostMapping("/run")
    public void run() {
        notificationScheduler.runDailyNotificationJob();
    }

}
