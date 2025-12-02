package pl.kurs.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import pl.kurs.entity.SubscriptionNotification;

import java.util.stream.Stream;

public interface SubscriptionNotificationRepository extends JpaRepository<SubscriptionNotification, Long> {

    @Query("""
            SELECT sn
            FROM SubscriptionNotification sn
            JOIN FETCH sn.client
            JOIN FETCH sn.book b
            JOIN FETCH b.category
            JOIN FETCH b.author
            ORDER BY sn.client.id
            """)
    @QueryHints({
            @QueryHint(name = "org.hibernate.fetchSize", value = "100"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true"),
            @QueryHint(name = "org.hibernate.cacheable", value = "false")
    })
    Stream<SubscriptionNotification> streamSubscriptionNotification();

}
