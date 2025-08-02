package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.entity.SubscriptionType;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.mapper.SubscriptionMapper;
import pl.kurs.repository.SubscriptionRepository;

import java.util.Arrays;
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
    void shouldCreateSubscription() {
        //given
        SubscriptionDto subscriptionDto = createSubscriptionDto();
        Subscription subscription = createSubscription();
        Subscription savedSubscription = createSavedSubscription();
        Client client = createClient();
        SubscriptionDto expectedDto = createExpectedSubscriptionDto();

        given(clientServiceMock.getVerifiedClientById(1L)).willReturn(client);
        given(subscriptionMapperMock.dtoToEntity(subscriptionDto)).willReturn(subscription);
        given(subscriptionRepositoryMock.save(any(Subscription.class))).willReturn(savedSubscription);
        given(subscriptionMapperMock.entityToDto(savedSubscription)).willReturn(expectedDto);

        //when
        SubscriptionDto result = subscriptionService.createSubscription(subscriptionDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getClientId()).isEqualTo(1L);
        assertThat(result.getSubscriptionType()).isEqualTo("CATEGORY");
        assertThat(result.getSubscriptionValue()).isEqualTo("Fantasy");
    }

    @Test
    void shouldThrowExceptionWhenClientNotFound() {
        //given
        SubscriptionDto subscriptionDto = createSubscriptionDto();

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
    void shouldReturnEmailsWhenSubscriptionsFound() {
        //given
        SubscriptionType subscriptionType = SubscriptionType.CATEGORY;
        String name = "Fantasy";
        List<String> expectedEmails = Arrays.asList(
                "user1@example.com",
                "user2@example.com",
                "user3@example.com"
        );

        given(subscriptionRepositoryMock.findEmailsBySubscriptionTypeAndSubscriptionValue(subscriptionType, name))
                .willReturn(expectedEmails);

        //when
        List<String> result = subscriptionService.findEmailsBySubscriptionTypeAndValue(subscriptionType, name);

        //then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result).containsExactlyElementsOf(expectedEmails);
    }

    private Client createClient() {
        return new Client(1L, "Jan", "Kowalski", "test@example.com", "Warszawa", true, null, null);
    }

    private SubscriptionDto createSubscriptionDto() {
        return new SubscriptionDto(null, 1L, "CATEGORY", "Fantasy");
    }

    private SubscriptionDto createExpectedSubscriptionDto() {
        return new SubscriptionDto(1L, 1L, "CATEGORY", "Fantasy");
    }

    private Subscription createSubscription() {
        return Subscription.builder()
                .client(createClient())
                .subscriptionType(SubscriptionType.CATEGORY)
                .subscriptionValue("Fantasy")
                .build();
    }

    private Subscription createSavedSubscription() {
        return Subscription.builder()
                .id(1L)
                .client(createClient())
                .subscriptionType(SubscriptionType.CATEGORY)
                .subscriptionValue("Fantasy")
                .build();
    }
}