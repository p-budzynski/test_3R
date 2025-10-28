package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import pl.kurs.entity.Book;
import pl.kurs.entity.Client;
import pl.kurs.entity.SubscriptionNotification;

import java.util.List;

public interface SubscriptionNotificationRepository extends JpaRepository<SubscriptionNotification, Long> {

    @Query("""
        SELECT DISTINCT sn.client
        FROM SubscriptionNotification sn
    """)
    List<Client> findClientsWithNotifications();

    @Query("""
        SELECT sn.book
        FROM SubscriptionNotification sn
        WHERE sn.client.id = :clientId
    """)
    List<Book> findBooksByClientId(@Param("clientId") Long clientId);

}
