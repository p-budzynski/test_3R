package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.entity.SubscriptionType;

import static org.assertj.core.api.Assertions.assertThat;

public class SubscriptionMapperTest {
    private final SubscriptionMapper subscriptionMapper = Mappers.getMapper(SubscriptionMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        Subscription testSubscription = createSubscriptionTest();
        SubscriptionDto testSubscriptionDto = createSubscriptionDtoTest();

        //when
        SubscriptionDto dto = subscriptionMapper.entityToDto(testSubscription);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testSubscriptionDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //given
        Subscription testSubscription = createSubscriptionTest();
        SubscriptionDto testSubscriptionDto = createSubscriptionDtoTest();

        //when
        Subscription entity = subscriptionMapper.dtoToEntity(testSubscriptionDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("client")
                .isEqualTo(testSubscription);
    }

    @Test
    void shouldMapDtoToEntityWhenSubscriptionTypeIsNull() {
        //given
        SubscriptionDto testSubscriptionDto = createSubscriptionDtoTest();
        testSubscriptionDto.setSubscriptionType(null);
        testSubscriptionDto.setSubscriptionValue(null);

        //when
        Subscription entity = subscriptionMapper.dtoToEntity(testSubscriptionDto);

        //then
        assertThat(entity.getId()).isEqualTo(testSubscriptionDto.getId());
        assertThat(entity.getSubscriptionType()).isNull();
        assertThat(entity.getSubscriptionValue()).isNull();
    }

    @Test
    void shouldReturnNullWhenEntityToDtoGivenNull() {
        //when then
        assertThat(subscriptionMapper.entityToDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoToEntityGivenNull() {
        //when then
        assertThat(subscriptionMapper.dtoToEntity(null)).isNull();
    }

    @Test
    void shouldReturnNullClientIdsWhenFieldsAreNull() {
        //given
        Subscription testSubscription = createSubscriptionTest();
        testSubscription.setClient(null);

        //when
        SubscriptionDto dto = subscriptionMapper.entityToDto(testSubscription);

        //then
        assertThat(dto.getClientId()).isNull();
        assertThat(dto.getSubscriptionType()).isEqualTo(testSubscription.getSubscriptionType().toString());
        assertThat(dto.getSubscriptionValue()).isEqualTo(testSubscription.getSubscriptionValue());
    }

    @Test
    void shouldReturnNullSubscriptionTypeWhenFieldsAreNull() {
        //given
        Subscription testSubscription = createSubscriptionTest();
        testSubscription.setSubscriptionType(null);
        testSubscription.setSubscriptionValue(null);

        //when
        SubscriptionDto dto = subscriptionMapper.entityToDto(testSubscription);

        //then
        assertThat(dto.getClientId()).isEqualTo(testSubscription.getClient().getId());
        assertThat(dto.getSubscriptionType()).isNull();
        assertThat(dto.getSubscriptionValue()).isNull();
    }

    private Subscription createSubscriptionTest() {
        Client clientTest = new Client(1L, "John", "Cena", "j.cena@mail.com", "Tampa", true, null, null);
        return new Subscription(1L, clientTest, SubscriptionType.CATEGORY, "Science");
    }

    private SubscriptionDto createSubscriptionDtoTest() {
        return new SubscriptionDto(1L, 1L, "CATEGORY", "Science");
    }
}