package pl.kurs.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class BookCreatedEvent extends ApplicationEvent {
    private final Long bookId;

    public BookCreatedEvent(Object source, Long bookId) {
        super(source);
        this.bookId = bookId;
    }

}