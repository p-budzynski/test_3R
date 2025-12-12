package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import pl.kurs.entity.MessageConfig;
import pl.kurs.repository.MessageConfigRepository;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@SpringJUnitConfig
@EnableCaching
public class MessageConfigServiceTest {

    @Configuration
    static class TestConfig {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("emailTemplates");
        }

        @Bean
        MessageConfigService messageConfigService(MessageConfigRepository repository) {
            return new MessageConfigService(repository);
        }
    }

    @MockitoBean
    private MessageConfigRepository messageConfigRepositoryMock;

    @Autowired
    private MessageConfigService messageConfigService;

    @Test
    void shouldCacheResultAndCallRepositoryOnlyOneForFindMessageConfigByCode() {
        //given
        String code = "TestCode";
        MessageConfig messageConfig = new MessageConfig(1L, code, "TestSubject", "TestBody");

        when(messageConfigRepositoryMock.findByCode(code)).thenReturn(Optional.of(messageConfig));

        //when
        MessageConfig first = messageConfigService.findMessageConfigByCode(code);
        MessageConfig second = messageConfigService.findMessageConfigByCode(code);

        //then
        assertThat(first).isEqualTo(second);
        verify(messageConfigRepositoryMock, times(1)).findByCode(code);
    }

    @Test
    void shouldThrowExceptionWhenConfigNotFound() {
        //given
        String code = "UNKNOWN";

        when(messageConfigRepositoryMock.findByCode(code)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> messageConfigService.findMessageConfigByCode(code))
                .isInstanceOf(NoSuchElementException.class)
                .hasMessage("Message Config not found for code: " + code);
    }
}