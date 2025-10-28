package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import pl.kurs.config.NotificationProperties;
import pl.kurs.entity.Book;
import pl.kurs.entity.Client;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender mailSender;
    private final NotificationProperties notificationProperties;

    public void sendVerificationEmail(String email, String token) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(notificationProperties.getEmail());
            message.setTo(email);
            message.setSubject("Confirm your e-mail address!");
            message.setText(STR."Click the link to confirm your e-mail: \{notificationProperties.getVerificationUrl()}\{token}");

            mailSender.send(message);
            log.info("Verification e-mail sent to: {}", email);
        } catch (Exception ex) {
            log.error("Error sending e-mail to: {}", email, ex);
        }
    }

    public void sendNewBookNotifications(Client client, List<Book> books) {
        try {
            StringBuilder body = new StringBuilder();
            body.append("Hello ").append(client.getFirstName()).append(",\n\n");
            body.append("We’ve added new books that might interest you:\n\n");

            for (Book book : books) {
                body.append(book.getTitle())
                        .append(" — ")
                        .append(book.getAuthor().getName())
                        .append(" (").append(book.getCategory().getName()).append(")\n");
            }

            body.append("\nVisit our library to explore them!\n\n");
            body.append("Best regards,\nYour Library Team");

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(notificationProperties.getEmail());
            message.setTo(client.getEmail());
            message.setSubject("New books in the library!");
            message.setText(body.toString());

            mailSender.send(message);
            log.info("Email with new books sent to {}", client.getEmail());
        } catch (Exception ex) {
            log.error("Error sending e-mail to: {}", client.getEmail(), ex);
        }
    }

}
