package pl.kurs.listener;

import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import pl.kurs.event.ClientVerificationEvent;
import pl.kurs.service.MailService;

@Component
@RequiredArgsConstructor
public class ClientVerificationEventListener {

    private final MailService mailService;

    @EventListener
    public void handleClientVerification(ClientVerificationEvent event) {
        mailService.sendVerificationEmail(event.getMail(), event.getToken());
    }

}
