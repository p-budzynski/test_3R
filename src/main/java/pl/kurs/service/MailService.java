package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.kurs.config.NotificationProperties;
import pl.kurs.entity.Book;
import pl.kurs.entity.Client;
import pl.kurs.entity.MessageConfig;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;
    private final MessageConfigService messageConfigService;

    public void sendVerificationEmail(String email, String token) {
        try {
            MessageConfig template = messageConfigService.findMessageConfigByCode("ACCOUNT_ACTIVATION");

            Map<String, String> variables = Map.of(
                    "verificationUrl", notificationProperties.getVerificationUrl(),
                    "token", token
            );

            String body = getMessageBody(template.getBody(), variables);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(notificationProperties.getEmail());
            message.setTo(email);
            message.setSubject(template.getSubject());
            message.setText(body);

            mailSender.send(message);
            log.info("Verification e-mail sent to: {}", email);
        } catch (Exception ex) {
            log.error("Error sending e-mail to: {}", email, ex);
        }
    }

    public void sendNewBookNotifications(Client client, List<Book> books) {
        try {
            MessageConfig template = messageConfigService.findMessageConfigByCode("NEW_BOOKS");

            String bookList = books.stream()
                    .map(book -> STR."• \{book.getTitle()} — \{book.getAuthor().getName()} (\{book.getCategory().getName()})")
            .collect(Collectors.joining("\n"));

            Map<String, String> variables = Map.of(
                    "firstName", client.getFirstName(),
                    "bookList", bookList
            );

            String body = getMessageBody(template.getBody(), variables)
                    .replace("\\n", "\n");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(notificationProperties.getEmail());
            message.setTo(client.getEmail());
            message.setSubject(template.getSubject());
            message.setText(body);

            mailSender.send(message);
            log.info("Email with new books sent to {}", client.getEmail());
        } catch (Exception ex) {
            log.error("Error sending e-mail to: {}", client.getEmail(), ex);
        }
    }

    private String getMessageBody(String body, Map<String, String> variables) {
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            String placeholder = STR."{{\{entry.getKey()}}}";
            body = body.replace(placeholder, entry.getValue());
        }
        return body;
    }

}
