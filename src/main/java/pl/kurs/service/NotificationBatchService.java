package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.config.NotificationProperties;
import pl.kurs.messaging.producer.EmailProducer;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationBatchService {
    private final SubscriptionService subscriptionService;
    private final EmailProducer emailProducer;
    private final NotificationProperties notificationProperties;

    @Async("notificationExecutor")
    @Transactional(readOnly = true)
    public CompletableFuture<Integer> processBatch(LocalDate date, int page) {
        PageRequest pageRequest = PageRequest.of(page, notificationProperties.getBatchSize());
        Slice<Object[]> batch = subscriptionService.findEmailsAndBooksForDatePaginated(date, pageRequest);

        AtomicInteger processedCount = new AtomicInteger(0);

        batch.getContent().forEach(row -> {
            try {
                String email = (String) row[0];
                String booksList = (String) row[1];

                String subject = "New books in the library - " + date;
                String content = "Here are the new books added to the library today, matching your subscriptions:\n\n" + booksList;

                emailProducer.sendDailyBookList(email, subject, content);
                processedCount.incrementAndGet();

            } catch (Exception e) {
                log.error("Error processing notification for batch page {}", page, e);
            }
        });

        log.debug("Processed batch page {} with {} notifications", page, processedCount.get());
        return CompletableFuture.completedFuture(processedCount.get());
    }

    public void processAllNotifications(LocalDate date) {
        int page = 0;
        int totalProcessed = 0;
        boolean hasMore = true;

        while (hasMore) {
            try {
                CompletableFuture<Integer> batchResult = processBatch(date, page);
                int batchProcessed = batchResult.get();
                totalProcessed += batchProcessed;

                if (batchProcessed < notificationProperties.getBatchSize()) {
                    hasMore = false;
                }

                page++;

                Thread.sleep(100);

            } catch (Exception e) {
                log.error("Error processing batch page {}", page, e);
                break;
            }
        }

        log.info("Daily notification processing completed. Total processed: {}", totalProcessed);
    }
}

