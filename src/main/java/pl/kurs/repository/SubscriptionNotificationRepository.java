package pl.kurs.repository;

import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import pl.kurs.entity.SubscriptionNotification;

import java.util.List;
import java.util.stream.Stream;

public interface SubscriptionNotificationRepository extends JpaRepository<SubscriptionNotification, Long> {

    @Query("SELECT sn FROM SubscriptionNotification sn")
    @QueryHints({
            @QueryHint(name = "org.hibernate.fetchSize", value = "1000"),
            @QueryHint(name = "org.hibernate.readOnly", value = "true")})
    Stream<SubscriptionNotification> streamSubscriptionNotification();

}
