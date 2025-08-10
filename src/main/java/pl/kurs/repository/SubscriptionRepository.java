package pl.kurs.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kurs.entity.Subscription;

import java.time.LocalDate;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query(value = """
            SELECT DISTINCT c.email, 
                   STRING_AGG(CONCAT(b.title, ' - ', b.author, ' (', cat.name, ')'), '\n') as books
            FROM subscriptions s
            JOIN clients c ON s.client_id = c.id AND c.email_verified = true
            JOIN books b ON b.added_date = :date
            JOIN categories cat ON b.book_category = cat.id
            WHERE ((s.subscription_type = 'AUTHOR' AND s.subscription_value = b.author)
            OR (s.subscription_type = 'CATEGORY' AND s.subscription_value = cat.name))
            GROUP BY c.email
            """, nativeQuery = true)
    Slice<Object[]> findEmailsAndBooksForDatePaginated(@Param("date") LocalDate date, Pageable pageable);

}
