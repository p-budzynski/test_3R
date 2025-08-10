package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.entity.SubscriptionType;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.mapper.SubscriptionMapper;
import pl.kurs.repository.SubscriptionRepository;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
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
    void shouldReturnEmailsAndBooksWhenSubscriptionsFoundForDate() {
        //given
        LocalDate testDate = LocalDate.of(2025, 1, 15);
        PageRequest pageRequest = PageRequest.of(0, 1000);

        Object[] row1 = {"user1@example.com", "Book 1 - Author 1 (Fantasy)\nBook 2 - Author 2 (Fantasy)"};
        Object[] row2 = {"user2@example.com", "Book 3 - Author 3 (Sci-Fi)"};
        Object[] row3 = {"user3@example.com", "Book 1 - Author 1 (Fantasy)"};

        List<Object[]> mockContent = Arrays.asList(row1, row2, row3);
        Slice<Object[]> mockSlice = new SliceImpl<>(mockContent, pageRequest, false);

        given(subscriptionRepositoryMock.findEmailsAndBooksForDatePaginated(testDate, pageRequest))
                .willReturn(mockSlice);

        //when
        Slice<Object[]> result = subscriptionService.findEmailsAndBooksForDatePaginated(testDate, pageRequest);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(3);

        Object[] firstRow = result.getContent().get(0);
        assertThat(firstRow[0]).isEqualTo(row1[0]);
        assertThat(firstRow[1]).isEqualTo(row1[1]);

        Object[] secondRow = result.getContent().get(1);
        assertThat(secondRow[0]).isEqualTo(row2[0]);
        assertThat(secondRow[1]).isEqualTo(row2[1]);

        Object[] thirdRow = result.getContent().get(2);
        assertThat(thirdRow[0]).isEqualTo(row3[0]);
        assertThat(thirdRow[1]).isEqualTo(row3[1]);

        assertThat(result.hasNext()).isFalse();
        assertThat(result.getPageable()).isEqualTo(pageRequest);
    }

    @Test
    void shouldReturnEmptySliceWhenNoSubscriptionsFound() {
        //given
        LocalDate testDate = LocalDate.of(2025, 1, 15);
        PageRequest pageRequest = PageRequest.of(0, 1000);

        Slice<Object[]> emptySlice = new SliceImpl<>(Collections.emptyList(), pageRequest, false);

        given(subscriptionRepositoryMock.findEmailsAndBooksForDatePaginated(testDate, pageRequest))
                .willReturn(emptySlice);

        //when
        Slice<Object[]> result = subscriptionService.findEmailsAndBooksForDatePaginated(testDate, pageRequest);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.hasNext()).isFalse();
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