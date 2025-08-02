package pl.kurs.messaging.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;
import pl.kurs.configuration.RabbitConfig;
import pl.kurs.messaging.dto.EmailMessage;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailConsumer {
    private final JavaMailSender mailSender;

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
            log.error("Failed to send email to: {} (type: {})",
                    emailMessage.getTo(), emailMessage.getType(), e);
            throw new RuntimeException("Email sending failed", e);
        }
    }
}
