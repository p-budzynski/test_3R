package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import pl.kurs.entity.MessageConfig;
import pl.kurs.repository.MessageConfigRepository;

import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MessageConfigService {
    private final MessageConfigRepository messageConfigRepository;

    @Cacheable(value = "emailTemplates", key = "#code")
    public MessageConfig findMessageConfigByCode(String code) {
        return messageConfigRepository.findByCode(code)
                .orElseThrow(() -> new NoSuchElementException(STR."Message Config not found for code: \{code}"));
    }

}
