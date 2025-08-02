package pl.kurs.messaging.producer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import pl.kurs.configuration.NotificationProperties;
import pl.kurs.configuration.RabbitConfig;
import pl.kurs.messaging.dto.EmailMessage;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailProducer {
    private final RabbitTemplate rabbitTemplate;
    private final NotificationProperties notificationProperties;

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

    public void sendNewBookNotifications(List<String> emails, String bookTitle, String author, String category) {
        String content = String.format(
                "New book added:\n\nTitle: %s\nAuthor: %s\nCategory: %s\n\n" +
                "Check it out at the library!",
                bookTitle, author, category
        );

        for (String email : emails) {
            EmailMessage message = EmailMessage.builder()
                    .to(email)
                    .subject("New book in library!")
                    .content(content)
                    .type(EmailMessage.EmailType.NEW_BOOK_NOTIFICATION)
                    .build();

            rabbitTemplate.convertAndSend(RabbitConfig.EMAIL_QUEUE, message);
        }
        log.info("Queued {} new book notifications", emails.size());
    }

}
