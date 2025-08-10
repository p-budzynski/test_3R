package pl.kurs.messaging.producer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import pl.kurs.config.NotificationProperties;
import pl.kurs.config.RabbitConfig;
import pl.kurs.messaging.dto.EmailMessage;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmailProducerTest {

    @Mock
    private RabbitTemplate rabbitTemplateMock;

    @Mock
    private NotificationProperties notificationPropertiesMock;

    @InjectMocks
    private EmailProducer emailProducer;

    private final String testEmail = "test@example.com";

    @Test
    void shouldSendVerificationEmail() {
        //given
        String token = "abc123xyz";
        String verificationUrl = "http://localhost:8080/verify?token=";
        when(notificationPropertiesMock.getVerificationUrl()).thenReturn(verificationUrl);

        //when
        emailProducer.sendVerificationEmail(testEmail, token);

        //then
        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(rabbitTemplateMock).convertAndSend(eq(RabbitConfig.EMAIL_QUEUE), messageCaptor.capture());

        EmailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).isEqualTo(testEmail);
        assertThat(capturedMessage.getSubject()).isEqualTo("Confirm your email address!");
        assertThat(capturedMessage.getContent()).isEqualTo("Click the link to confirm your email: " + verificationUrl + token);
        assertThat(capturedMessage.getType()).isEqualTo(EmailMessage.EmailType.VERIFICATION);
        assertThat(capturedMessage.getRetryCount()).isEqualTo(0);
    }

    @Test
    void shouldSendDailyBookListEmail() {
        //given
        String subject = "New books in the library - 2025-08-10";
        String content = "Here are the new books:\n\nClean Code\nDesign Patterns";

        //when
        emailProducer.sendDailyBookList(testEmail, subject, content);

        //then
        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(rabbitTemplateMock).convertAndSend(eq(RabbitConfig.EMAIL_QUEUE), messageCaptor.capture());

        EmailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getTo()).isEqualTo(testEmail);
        assertThat(capturedMessage.getSubject()).isEqualTo(subject);
        assertThat(capturedMessage.getContent()).isEqualTo(content);
        assertThat(capturedMessage.getType()).isEqualTo(EmailMessage.EmailType.NEW_BOOK_NOTIFICATION);
        assertThat(capturedMessage.getRetryCount()).isEqualTo(0);
    }

    @Test
    void shouldUseCorrectVerificationUrlFromProperties() {
        //given
        String customUrl = "https://myapp.com/verify?token=";
        when(notificationPropertiesMock.getVerificationUrl()).thenReturn(customUrl);
        String token = "token123";

        //when
        emailProducer.sendVerificationEmail(testEmail, token);

        //then
        ArgumentCaptor<EmailMessage> messageCaptor = ArgumentCaptor.forClass(EmailMessage.class);
        verify(rabbitTemplateMock).convertAndSend(eq(RabbitConfig.EMAIL_QUEUE), messageCaptor.capture());

        EmailMessage capturedMessage = messageCaptor.getValue();
        assertThat(capturedMessage.getContent()).contains(customUrl + token);
    }

    @Test
    void shouldSendToCorrectQueue() {
        //given
        String token = "test-token";

        //when
        emailProducer.sendVerificationEmail(testEmail, token);

        //then
        verify(rabbitTemplateMock).convertAndSend(eq(RabbitConfig.EMAIL_QUEUE), any(EmailMessage.class));
    }
}