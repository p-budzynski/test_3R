package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import pl.kurs.entity.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
}
