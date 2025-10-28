package pl.kurs.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ClientVerificationEvent extends ApplicationEvent {
    private final String mail;
    private final String token;

    public ClientVerificationEvent(Object source, String mail, String token) {
        super(source);
        this.mail = mail;
        this.token = token;
    }

}
