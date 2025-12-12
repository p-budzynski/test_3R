package pl.kurs.listener;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.event.ClientVerificationEvent;
import pl.kurs.service.MailService;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ClientVerificationEventListenerTest {

    @Mock
    private MailService mailServiceMock;

    @InjectMocks
    private ClientVerificationEventListener eventListener;

    @Test
    void shouldCallMailServiceOnClientVerificationEvent() {
        //given
        String mail = "test@mail.com";
        String token = "test123";
        ClientVerificationEvent event = new ClientVerificationEvent(this, mail, token);

        //when
        eventListener.handleClientVerification(event);

        //then
        verify(mailServiceMock).sendVerificationEmail(mail, token);
    }
}