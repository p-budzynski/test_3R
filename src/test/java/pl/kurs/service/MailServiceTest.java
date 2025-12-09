package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import pl.kurs.config.NotificationProperties;
import pl.kurs.entity.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MailServiceTest {

    @Mock
    private JavaMailSender mailSenderMock;

    @Mock
    private NotificationProperties notificationPropertiesMock;

    @Mock
    private MessageConfigService messageConfigServiceMock;

    @InjectMocks
    private MailService mailService;

    private static final String SENDER_MAIL = "sender@mail.com";
    private static final String VERIFICATION_URL = "http://verifi.url/";

    @Test
    void shouldSendVerificationEmail() {
        //given
        String recipientEmail = "user@example.com";
        String token = "test_token_123";
        String subject = "Account activation";
        String bodyTemplate = "Hello, click on the verification link: {{verificationUrl}}?token={{token}}";
        String expectedBody = "Hello, click on the verification link: " + VERIFICATION_URL + "?token=" + token;

        MessageConfig mockTemplate = new MessageConfig();
        mockTemplate.setSubject(subject);
        mockTemplate.setBody(bodyTemplate);

        when(messageConfigServiceMock.findMessageConfigByCode("ACCOUNT_ACTIVATION")).thenReturn(mockTemplate);
        when(notificationPropertiesMock.getVerificationUrl()).thenReturn(VERIFICATION_URL);
        when(notificationPropertiesMock.getEmail()).thenReturn(SENDER_MAIL);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        //when
        mailService.sendVerificationEmail(recipientEmail, token);

        //then
        verify(mailSenderMock, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getFrom()).isEqualTo(SENDER_MAIL);
        assertThat(sentMessage.getTo()[0]).isEqualTo(recipientEmail);
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getText()).isEqualTo(expectedBody);
    }

    @Test
    void shouldHandleExceptionsWhenSendingVerificationEmailFails() {
        //given
        String recipientEmail = "user@example.com";
        String token = "test_token_123";

        MessageConfig mockTemplate = new MessageConfig();
        mockTemplate.setSubject("Test");
        mockTemplate.setBody("Body");

        when(messageConfigServiceMock.findMessageConfigByCode(anyString())).thenReturn(mockTemplate);
        when(notificationPropertiesMock.getVerificationUrl()).thenReturn(VERIFICATION_URL);
        when(notificationPropertiesMock.getEmail()).thenReturn(SENDER_MAIL);

        doThrow(new RuntimeException("Test Mail Exception - Verification Email"))
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when
        mailService.sendVerificationEmail(recipientEmail, token);

        //then
        verify(mailSenderMock, times(1)).send(any(SimpleMailMessage.class));
        verifyNoMoreInteractions(mailSenderMock);
    }

    @Test
    void shouldSendNewBookNotifications() {
        //given
        Client client = new Client(1L, "Jan", "Nowak", "j.nowak@mail.com", "Warsaw,", true, null, null);
        Author author = new Author(1L, "George Orwell", null);
        List<Book> books = List.of(
                new Book(1L, "Nineteen Eighty-Four", new Category(1L, "Science fiction"), 350, author),
                new Book(2L, "Animal Farm", new Category(2L, "Political satire"), 110, author));
        author.setBooks(books);

        String subject = "New books in the library!";
        String bodyTemplate = "Hello {{firstName}},\n\nWe’ve added new books that might interest you:\n\n{{bookList}}\n\nVisit our library to explore them!\n\nBest regards,\nYour Library Team!";

        String expectedBookList = books.stream()
                .map(book -> "• " + book.getTitle() + " — " + book.getAuthor().getName() + " (" + book.getCategory().getName() + ")")
                .collect(Collectors.joining("\n"));
        String expectedBody = "Hello " + client.getFirstName() + ",\n\nWe’ve added new books that might interest you:\n\n" + expectedBookList + "\n\nVisit our library to explore them!\n\nBest regards,\nYour Library Team!";

        MessageConfig mockTemplate = new MessageConfig();
        mockTemplate.setSubject(subject);
        mockTemplate.setBody(bodyTemplate);

        when(messageConfigServiceMock.findMessageConfigByCode("NEW_BOOKS")).thenReturn(mockTemplate);
        when(notificationPropertiesMock.getEmail()).thenReturn(SENDER_MAIL);
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);

        //when
        mailService.sendNewBookNotifications(client, books);

        //then
        verify(mailSenderMock, times(1)).send(messageCaptor.capture());

        SimpleMailMessage sentMessage = messageCaptor.getValue();
        assertThat(sentMessage.getFrom()).isEqualTo(SENDER_MAIL);
        assertThat(sentMessage.getTo()[0]).isEqualTo(client.getEmail());
        assertThat(sentMessage.getSubject()).isEqualTo(subject);
        assertThat(sentMessage.getText()).isEqualTo(expectedBody);
    }

    @Test
    void shouldHandleExceptionsWhenSendingNewBookNotificationsEmailFails() {
        //given
        Client client = new Client();
        client.setEmail("client@mail.com");
        client.setFirstName("Jan");
        List<Book> books = List.of();

        MessageConfig mockTemplate = new MessageConfig();
        mockTemplate.setSubject("Test");
        mockTemplate.setBody("Body {{firstName}} {{bookList}}");

        when(messageConfigServiceMock.findMessageConfigByCode(anyString())).thenReturn(mockTemplate);
        when(notificationPropertiesMock.getEmail()).thenReturn(SENDER_MAIL);

        doThrow(new RuntimeException("Test Mail Exception - New Books")).when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when
        mailService.sendNewBookNotifications(client, books);

        //then
        verify(mailSenderMock, times(1)).send(any(SimpleMailMessage.class));
        verifyNoMoreInteractions(mailSenderMock);
    }

}