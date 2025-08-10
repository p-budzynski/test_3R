package pl.kurs.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pl.kurs.config.NotificationProperties;
import pl.kurs.config.RabbitConfig;
import pl.kurs.messaging.dto.EmailMessage;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;
    private final NotificationProperties notificationProperties;

    @Async("emailExecutor")
    public void sendVerificationEmail(String email, String token) {
        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject("Confirm your email address!")
                .content("Click the link to confirm your email: " +
                         notificationProperties.getVerificationUrl() + token)
                .type(EmailMessage.EmailType.VERIFICATION)
                .build();

        rabbitTemplate.convertAndSend(RabbitConfig.EMAIL_QUEUE, message);
        log.info("Email message queued for: {}", email);
    }

    @Async("emailExecutor")
    public void sendDailyBookList(String email, String subject, String content) {
        EmailMessage message = EmailMessage.builder()
                .to(email)
                .subject(subject)
                .content(content)
                .type(EmailMessage.EmailType.NEW_BOOK_NOTIFICATION)
                .build();

        rabbitTemplate.convertAndSend(RabbitConfig.EMAIL_QUEUE, message);
        log.info("Daily book list queued for: {}", email);
    }

}
