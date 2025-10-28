package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pl.kurs.entity.Author;
import pl.kurs.entity.Category;
import pl.kurs.entity.Subscription;

import java.util.List;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    @Query("""
        SELECT s FROM Subscription s
        WHERE (s.author.id = :authorId)
           OR (s.category.id = :categoryId)
    """)
    List<Subscription> findByAuthorIdOrCategoryId(
            @Param("authorId") Long authorId, @Param("categoryId") Long categoryId);

}
