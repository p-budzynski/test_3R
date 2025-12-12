package pl.kurs.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.kurs.entity.MessageConfig;

import java.util.Optional;

public interface MessageConfigRepository extends JpaRepository<MessageConfig, Long> {
    Optional<MessageConfig> findByCode(String code);
}
