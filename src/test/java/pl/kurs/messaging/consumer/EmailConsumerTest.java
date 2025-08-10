package pl.kurs.messaging.consumer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import pl.kurs.config.NotificationProperties;
import pl.kurs.messaging.dto.EmailMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmailConsumerTest {

    @Mock
    private JavaMailSender mailSenderMock;

    @Mock
    private NotificationProperties notificationPropertiesMock;

    @InjectMocks
    private EmailConsumer emailConsumer;

    private final int maxRetries = 3;

    @Test
    void shouldSendEmailSuccessfully() {
        //given
        EmailMessage testEmailMessage = createTestEmailMessage();
        doNothing().when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when
        emailConsumer.handleEmailMessage(testEmailMessage);

        //then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSenderMock).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).containsExactly("test@example.com");
        assertThat(capturedMessage.getSubject()).isEqualTo("Test Subject");
        assertThat(capturedMessage.getText()).isEqualTo("Test Content");
    }

    @Test
    void shouldRetryWhenMailSendingFailsAndRetryCountBelowMax() {
        //given
        EmailMessage messageWithRetries = EmailMessage.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .type(EmailMessage.EmailType.NEW_BOOK_NOTIFICATION)
                .retryCount(1) // poniżej maxRetries (3)
                .build();
        when(notificationPropertiesMock.getMaxRetries()).thenReturn(maxRetries);
        doThrow(new MailException("SMTP server unavailable") {})
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when then
        assertThatThrownBy(() -> emailConsumer.handleEmailMessage(messageWithRetries))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Retry email sending");

        verify(mailSenderMock).send(any(SimpleMailMessage.class));
        assertThat(messageWithRetries.getRetryCount()).isEqualTo(2); // inkrementowany
    }

    @Test
    void shouldNotRetryWhenMaxRetriesReached() {
        //given
        EmailMessage messageAtMaxRetries = EmailMessage.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .type(EmailMessage.EmailType.VERIFICATION)
                .retryCount(maxRetries)
                .build();

        doThrow(new MailException("SMTP server unavailable") {})
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when
        emailConsumer.handleEmailMessage(messageAtMaxRetries);

        //then
        verify(mailSenderMock).send(any(SimpleMailMessage.class));
        assertThat(messageAtMaxRetries.getRetryCount()).isEqualTo(maxRetries);
    }

    @Test
    void shouldNotRetryWhenMaxRetriesExceeded() {
        //given
        EmailMessage messageOverMaxRetries = EmailMessage.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .type(EmailMessage.EmailType.NEW_BOOK_NOTIFICATION)
                .retryCount(maxRetries + 1)
                .build();

        doThrow(new MailException("SMTP server unavailable") {})
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when
        emailConsumer.handleEmailMessage(messageOverMaxRetries);

        //then
        verify(mailSenderMock).send(any(SimpleMailMessage.class));
        assertThat(messageOverMaxRetries.getRetryCount()).isEqualTo(maxRetries + 1);
    }

    @Test
    void shouldHandleVerificationEmailType() {
        //given
        EmailMessage verificationEmail = EmailMessage.builder()
                .to("verify@example.com")
                .subject("Confirm your email")
                .content("Click to verify")
                .type(EmailMessage.EmailType.VERIFICATION)
                .retryCount(0)
                .build();

        //when
        emailConsumer.handleEmailMessage(verificationEmail);

        //then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSenderMock).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).containsExactly("verify@example.com");
        assertThat(capturedMessage.getSubject()).isEqualTo("Confirm your email");
        assertThat(capturedMessage.getText()).isEqualTo("Click to verify");
    }

    @Test
    void shouldHandleNewBookNotificationEmailType() {
        //given
        EmailMessage bookNotification = EmailMessage.builder()
                .to("reader@example.com")
                .subject("New books available")
                .content("Check out these new books")
                .type(EmailMessage.EmailType.NEW_BOOK_NOTIFICATION)
                .retryCount(0)
                .build();

        //when
        emailConsumer.handleEmailMessage(bookNotification);

        //then
        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSenderMock).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).containsExactly("reader@example.com");
        assertThat(capturedMessage.getSubject()).isEqualTo("New books available");
        assertThat(capturedMessage.getText()).isEqualTo("Check out these new books");
    }

    @Test
    void shouldIncrementRetryCountOnFailure() {
        //given
        EmailMessage messageWithZeroRetries = EmailMessage.builder()
                .to("test@example.com")
                .subject("Test")
                .content("Test")
                .type(EmailMessage.EmailType.VERIFICATION)
                .retryCount(0)
                .build();
        when(notificationPropertiesMock.getMaxRetries()).thenReturn(maxRetries);
        doThrow(new MailException("Temporary failure") {})
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when then
        assertThatThrownBy(() -> emailConsumer.handleEmailMessage(messageWithZeroRetries))
                .isInstanceOf(RuntimeException.class);

        assertThat(messageWithZeroRetries.getRetryCount()).isEqualTo(1);
    }

    @Test
    void shouldUseMaxRetriesFromProperties() {
        //given
        int customMaxRetries = 5;
        when(notificationPropertiesMock.getMaxRetries()).thenReturn(customMaxRetries);

        EmailMessage messageAtCustomMax = EmailMessage.builder()
                .to("test@example.com")
                .subject("Test")
                .content("Test")
                .type(EmailMessage.EmailType.VERIFICATION)
                .retryCount(customMaxRetries)
                .build();

        doThrow(new MailException("SMTP failure") {})
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when
        emailConsumer.handleEmailMessage(messageAtCustomMax);

        //then
        verify(mailSenderMock).send(any(SimpleMailMessage.class));
        assertThat(messageAtCustomMax.getRetryCount()).isEqualTo(customMaxRetries);
    }

    @Test
    void shouldNotThrowWhenMaxRetriesReached() {
        //given
        EmailMessage messageAtMaxRetries = EmailMessage.builder()
                .to("test@example.com")
                .subject("Test")
                .content("Test")
                .type(EmailMessage.EmailType.VERIFICATION)
                .retryCount(maxRetries)
                .build();

        doThrow(new MailException("Permanent failure") {})
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when
        emailConsumer.handleEmailMessage(messageAtMaxRetries);

        //then
        verify(mailSenderMock).send(any(SimpleMailMessage.class));
    }

    @Test
    void shouldHandleRuntimeExceptionDuringEmailSending() {
        //given
        EmailMessage testEmailMessage = createTestEmailMessage();
        when(notificationPropertiesMock.getMaxRetries()).thenReturn(maxRetries);
        doThrow(new RuntimeException("Unexpected error"))
                .when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when then
        assertThatThrownBy(() -> emailConsumer.handleEmailMessage(testEmailMessage))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Retry email sending");

        verify(mailSenderMock).send(any(SimpleMailMessage.class));
        assertThat(testEmailMessage.getRetryCount()).isEqualTo(1);
    }

    @Test
    void shouldLogSuccessfulEmailSending() {
        //given
        EmailMessage testEmailMessage = createTestEmailMessage();
        doNothing().when(mailSenderMock).send(any(SimpleMailMessage.class));

        //when
        emailConsumer.handleEmailMessage(testEmailMessage);

        //then
        verify(mailSenderMock).send(any(SimpleMailMessage.class));
    }

    private EmailMessage createTestEmailMessage() {
        return EmailMessage.builder()
                .to("test@example.com")
                .subject("Test Subject")
                .content("Test Content")
                .type(EmailMessage.EmailType.VERIFICATION)
                .retryCount(0)
                .build();
    }
}