package pl.kurs.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.kurs.exception.InvalidSubscriptionException;

@Entity
@Table(name = "subscriptions",
        indexes = {
                @Index(name = "idx_subscription_client", columnList = "client_fk"),
                @Index(name = "idx_subscription_author", columnList = "author_fk"),
                @Index(name = "idx_subscription_category", columnList = "category_fk")
        })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_fk", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_fk")
    private Author author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_fk")
    private Category category;

    @PrePersist
    @PreUpdate
    private void validate() {
        if ((author == null && category == null) || (author != null && category != null)) {
            throw new InvalidSubscriptionException("Subscription must have either author OR category, but not both.");
        }
    }

}
