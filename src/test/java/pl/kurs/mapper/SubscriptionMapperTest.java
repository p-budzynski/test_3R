package pl.kurs.mapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Author;
import pl.kurs.entity.Category;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
public class SubscriptionMapperTest {

    private final SubscriptionMapper subscriptionMapper = Mappers.getMapper(SubscriptionMapper.class);
    private final AuthorMapper authorMapper = Mappers.getMapper(AuthorMapper.class);
    private final ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);
    private final CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    @BeforeEach
    void before() {
        ReflectionTestUtils.setField(subscriptionMapper,"authorMapper", authorMapper);
        ReflectionTestUtils.setField(subscriptionMapper, "clientMapper", clientMapper);
        ReflectionTestUtils.setField(subscriptionMapper, "categoryMapper", categoryMapper);
    }

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
                .isEqualTo(testSubscription);
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
    void shouldReturnNullAuthorIdsWhenFieldsAreNull() {
        //given
        Subscription testSubscription = createSubscriptionTest();
        testSubscription.setAuthor(null);

        //when
        SubscriptionDto dto = subscriptionMapper.entityToDto(testSubscription);

        //then
        assertThat(dto.getAuthorId()).isNull();
    }

    @Test
    void shouldReturnNullCategoryIdsWhenFieldsAreNull() {
        //given
        Subscription testSubscription = createSubscriptionTest();
        testSubscription.setCategory(null);

        //when
        SubscriptionDto dto = subscriptionMapper.entityToDto(testSubscription);

        //then
        assertThat(dto.getCategoryId()).isNull();
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
    }

    private Subscription createSubscriptionTest() {
        Client clientTest = new Client();
        clientTest.setId(1L);
        Author authorTest = new Author();
        authorTest.setId(1L);
        Category categoryTest = new Category();
        categoryTest.setId(1L);
        return new Subscription(1L, clientTest, authorTest, categoryTest);
    }

    private SubscriptionDto createSubscriptionDtoTest() {
        return new SubscriptionDto(1L, 1L, 1L, 1L);
    }
}