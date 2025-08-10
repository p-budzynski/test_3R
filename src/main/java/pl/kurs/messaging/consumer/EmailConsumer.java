package pl.kurs.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import pl.kurs.config.NotificationProperties;
import pl.kurs.config.RabbitConfig;
import pl.kurs.messaging.dto.EmailMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {
    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;

    @RabbitListener(queues = RabbitConfig.EMAIL_QUEUE)
    public void handleEmailMessage(EmailMessage emailMessage) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emailMessage.getTo());
            message.setSubject(emailMessage.getSubject());
            message.setText(emailMessage.getContent());

            mailSender.send(message);

            log.info("Email sent successfully to: {} (type: {})",
                    emailMessage.getTo(), emailMessage.getType());

        } catch (Exception e) {
            log.error("Failed to send email to: {} (type: {}), retry count: {}",
                    emailMessage.getTo(), emailMessage.getType(), emailMessage.getRetryCount(), e);

            if (emailMessage.getRetryCount() < notificationProperties.getMaxRetries()) {
                emailMessage.setRetryCount(emailMessage.getRetryCount() + 1);

                throw new RuntimeException("Retry email sending", e);
            } else {
                log.error("Max retries exceeded for email: {}", emailMessage.getTo());
            }
        }
    }
}
