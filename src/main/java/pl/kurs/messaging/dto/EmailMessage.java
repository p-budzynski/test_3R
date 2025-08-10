package pl.kurs.messaging.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmailMessage {
    private String to;
    private String subject;
    private String content;
    private EmailType type;
    private int retryCount = 0;

    public enum EmailType {
        VERIFICATION, NEW_BOOK_NOTIFICATION
    }
}
