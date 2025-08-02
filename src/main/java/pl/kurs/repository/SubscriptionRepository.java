package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kurs.entity.Subscription;
import pl.kurs.entity.SubscriptionType;

import java.util.List;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("SELECT DISTINCT c.email FROM Subscription s JOIN s.client c " +
           "WHERE s.subscriptionType = :subscriptionType AND s.subscriptionValue = :subscriptionValue " +
           "AND c.emailVerified = true")
    List<String> findEmailsBySubscriptionTypeAndSubscriptionValue(
            @Param("subscriptionType") SubscriptionType subscriptionType,
            @Param("subscriptionValue") String subscriptionValue);

}
