package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Author;
import pl.kurs.entity.Category;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.exception.InvalidSubscriptionException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.exception.SubscriptionAlreadyExistsException;
import pl.kurs.mapper.SubscriptionMapper;
import pl.kurs.repository.SubscriptionRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
class SubscriptionServiceTest {

    @Mock
    private SubscriptionRepository subscriptionRepositoryMock;

    @Mock
    private ClientService clientServiceMock;

    @Mock
    private SubscriptionMapper subscriptionMapperMock;

    @InjectMocks
    private SubscriptionService subscriptionService;

    @Test
    void shouldCreateSubscriptionWithAuthor() {
        //given
        SubscriptionDto subscriptionDto = new SubscriptionDto(null, 1L, 1L, null);
        Subscription subscription = createSubscriptionWithAuthor();
        Subscription savedSubscription = createSavedSubscriptionWithAuthor();
        Client client = createClient();
        SubscriptionDto expectedDto = new SubscriptionDto(1L, 1L, 1L, null);

        given(clientServiceMock.getVerifiedClientById(1L)).willReturn(client);
        given(subscriptionRepositoryMock.existsByClientIdAndAuthorId(1L, 1L)).willReturn(false);
        given(subscriptionMapperMock.dtoToEntity(subscriptionDto)).willReturn(subscription);
        given(subscriptionRepositoryMock.save(any(Subscription.class))).willReturn(savedSubscription);
        given(subscriptionMapperMock.entityToDto(savedSubscription)).willReturn(expectedDto);

        //when
        SubscriptionDto result = subscriptionService.createSubscription(subscriptionDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClientId()).isEqualTo(1L);
        assertThat(result.getAuthorId()).isEqualTo(1L);
        assertThat(result.getCategoryId()).isNull();
    }

    @Test
    void shouldCreateSubscriptionWithCategory() {
        //given
        SubscriptionDto subscriptionDto = new SubscriptionDto(null, 1L, null, 1L);
        Subscription subscription = createSubscriptionWithCategory();
        Subscription savedSubscription = createSavedSubscriptionWithCategory();
        Client client = createClient();
        SubscriptionDto expectedDto = new SubscriptionDto(1L, 1L, null, 1L);

        given(clientServiceMock.getVerifiedClientById(1L)).willReturn(client);
        given(subscriptionRepositoryMock.existsByClientIdAndCategoryId(1L, 1L)).willReturn(false);
        given(subscriptionMapperMock.dtoToEntity(subscriptionDto)).willReturn(subscription);
        given(subscriptionRepositoryMock.save(any(Subscription.class))).willReturn(savedSubscription);
        given(subscriptionMapperMock.entityToDto(savedSubscription)).willReturn(expectedDto);

        //when
        SubscriptionDto result = subscriptionService.createSubscription(subscriptionDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClientId()).isEqualTo(1L);
        assertThat(result.getAuthorId()).isNull();
        assertThat(result.getCategoryId()).isEqualTo(1L);
    }

    @Test
    void shouldThrowExceptionWhenSubscriptionAlreadyExists() {
        //given
        Client client = createClient();
        SubscriptionDto subscriptionDto = new SubscriptionDto(null, client.getId(), 1L, null);

        given(clientServiceMock.getVerifiedClientById(client.getId())).willReturn(client);
        given(subscriptionRepositoryMock.existsByClientIdAndAuthorId(client.getId(), 1L)).willReturn(true);

        //when then
        assertThatThrownBy(() -> subscriptionService.createSubscription(subscriptionDto))
                .isInstanceOf(SubscriptionAlreadyExistsException.class)
                .hasMessage("Subscription for client id: " + client.getId() + " is already exists");
    }

    @Test
    void shouldThrowExceptionWhenSubscriptionHasAuthorNullAndCategoryNull() {
        //given
        Client client = createClient();
        SubscriptionDto subscriptionDto = new SubscriptionDto(null, client.getId(), null, null);

        given(clientServiceMock.getVerifiedClientById(client.getId())).willReturn(client);

        //when then
        assertThatThrownBy(() -> subscriptionService.createSubscription(subscriptionDto))
                .isInstanceOf(InvalidSubscriptionException.class)
                .hasMessage("Subscription must have either authorId OR categoryId, but not both.");
    }

    @Test
    void shouldThrowExceptionWhenSubscriptionHasAuthorAndCategory() {
        //given
        Client client = createClient();
        SubscriptionDto subscriptionDto = new SubscriptionDto(null, client.getId(), 1L, 1L);

        given(clientServiceMock.getVerifiedClientById(client.getId())).willReturn(client);

        //when then
        assertThatThrownBy(() -> subscriptionService.createSubscription(subscriptionDto))
                .isInstanceOf(InvalidSubscriptionException.class)
                .hasMessage("Subscription must have either authorId OR categoryId, but not both.");
    }

    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        //given
        SubscriptionDto subscriptionDto = new SubscriptionDto(1L, 1L, 1L, null);

        given(clientServiceMock.getVerifiedClientById(1L))
                .willThrow(new ResourceNotFoundException("Client not found"));

        //when then
        assertThatThrownBy(() -> subscriptionService.createSubscription(subscriptionDto))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found");
    }

    @Test
    void shouldCancelSubscription() {
        //given
        Long subscriptionId = 1L;

        //when
        subscriptionService.cancelSubscriptionById(subscriptionId);

        //then
        then(subscriptionRepositoryMock).should(times(1)).deleteById(subscriptionId);
    }

    @Test
    void shouldReturnSubscriptionForAuthor() {
        //given
        List<Subscription> subscriptions = List.of(createSubscriptionWithAuthor());

        given(subscriptionRepositoryMock.findByAuthorIdOrCategoryId(1L, null))
                .willReturn(subscriptions);

        //when
        List<Subscription> result = subscriptionService.findByAuthorIdOrCategoryId(1L, null);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAuthor().getId()).isEqualTo(1L);
        assertThat(result.get(0).getCategory()).isNull();
    }

    @Test
    void shouldReturnSubscriptionForCategory() {
        //given
        List<Subscription> subscriptions = List.of(createSubscriptionWithCategory());

        given(subscriptionRepositoryMock.findByAuthorIdOrCategoryId(null, 1L))
                .willReturn(subscriptions);

        //when
        List<Subscription> result = subscriptionService.findByAuthorIdOrCategoryId(null, 1L);

        //then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getCategory().getId()).isEqualTo(1L);
        assertThat(result.get(0).getAuthor()).isNull();
    }

    private Client createClient() {
        return new Client(1L, "Jan", "Kowalski", "test@example.com", "Warszawa", true, null, null);
    }

    private Author createAuthor() {
        return new Author(1L, "AuthorTest", null);
    }

    private Category createCategory() {
        return new Category(1L, "CategoryTest");
    }

    private Subscription createSubscriptionWithAuthor() {
        return Subscription.builder()
                .client(createClient())
                .author(createAuthor())
                .category(null)
                .build();
    }

    private Subscription createSubscriptionWithCategory() {
        return Subscription.builder()
                .client(createClient())
                .author(null)
                .category(createCategory())
                .build();
    }

    private Subscription createSavedSubscriptionWithAuthor() {
        return Subscription.builder()
                .id(1L)
                .client(createClient())
                .author(createAuthor())
                .category(null)
                .build();
    }

    private Subscription createSavedSubscriptionWithCategory() {
        return Subscription.builder()
                .id(1L)
                .client(createClient())
                .author(null)
                .category(createCategory())
                .build();
    }

}