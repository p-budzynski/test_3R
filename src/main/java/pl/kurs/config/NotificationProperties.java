package pl.kurs.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "notification")
@Data
@Component
public class NotificationProperties {
    private String verificationUrl;
    private String email;
}
