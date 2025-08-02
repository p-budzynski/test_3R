package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.dto.ClientDto;
import pl.kurs.entity.Client;
import pl.kurs.exception.EmailNotVerifiedException;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.mapper.ClientMapper;
import pl.kurs.messaging.producer.EmailProducer;
import pl.kurs.repository.ClientRepository;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {

    @Mock
    private ClientRepository clientRepositoryMock;

    @Mock
    private EmailProducer emailProducerMock;

    @Mock
    private ClientMapper clientMapperMock;

    @InjectMocks
    private ClientService clientService;

    @Test
    void shouldRegisterClientSuccessfully() {
        //given
        ClientDto clientDto = createClientDto();
        Client client = createClient();
        Client savedClient = createSavedClient();
        String expectedToken = "generated-uuid-token";

        try (MockedStatic<UUID> uuidMock = mockStatic(UUID.class)) {
            UUID mockUuid = mock(UUID.class);
            uuidMock.when(UUID::randomUUID).thenReturn(mockUuid);
            when(mockUuid.toString()).thenReturn(expectedToken);

            when(clientMapperMock.dtoToEntity(clientDto)).thenReturn(client);
            when(clientRepositoryMock.save(any(Client.class))).thenReturn(savedClient);
            when(clientMapperMock.entityToDto(savedClient)).thenReturn(clientDto);

            //when
            ClientDto result = clientService.registerClient(clientDto);

            //then
            assertThat(result).isEqualTo(clientDto);
        }
    }

    @Test
    void shouldVerifyEmailSuccessfully() {
        //given
        String token = "valid-token";
        Client unverifiedClient = createClientWithToken(token);

        when(clientRepositoryMock.findByVerificationToken(token)).thenReturn(Optional.of(unverifiedClient));
        when(clientRepositoryMock.save(any(Client.class))).thenReturn(unverifiedClient);

        //when
        boolean result = clientService.verifyEmail(token);

        //then
        assertThat(result).isTrue();
    }

    @Test
    void shouldGetClientByIdSuccessfully() {
        //given
        Long clientId = 1L;
        Client savedClient = createSavedClient();
        when(clientRepositoryMock.findById(clientId)).thenReturn(Optional.of(savedClient));

        //when
        Client result = clientService.getClientById(clientId);

        //then
        assertThat(result).isEqualTo(savedClient);
    }

    @Test
    void shouldThrowResourceNotFoundExceptionWhenClientNotFoundById() {
        //given
        Long clientId = 999L;
        when(clientRepositoryMock.findById(clientId)).thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> clientService.getClientById(clientId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Client not found with id: " + clientId);
    }

    @Test
    void shouldGetVerifiedClientByIdSuccessfully() {
        //given
        Long clientId = 1L;
        Client verifiedClient = createVerifiedClient(clientId);

        when(clientRepositoryMock.findById(clientId)).thenReturn(Optional.of(verifiedClient));

        //when
        Client result = clientService.getVerifiedClientById(clientId);

        //then
        assertThat(result).isEqualTo(verifiedClient);
        assertThat(result.getEmailVerified()).isTrue();
    }

    @Test
    void shouldThrowEmailNotVerifiedExceptionWhenClientEmailNotVerified() {
        //given
        Long clientId = 1L;
        Client unverifiedClient = createUnverifiedClient(clientId);

        when(clientRepositoryMock.findById(clientId)).thenReturn(Optional.of(unverifiedClient));

        //when then
        assertThatThrownBy(() -> clientService.getVerifiedClientById(clientId))
                .isInstanceOf(EmailNotVerifiedException.class)
                .hasMessage("Email must be verified before creating subscription");
    }

    private ClientDto createClientDto() {
        return new ClientDto(1L, "Jan", "Kowalski", "test@example.com", false, "Warszawa");
    }

    private Client createClient() {
        return Client.builder()
                .firstName("Jan")
                .lastName("Kowalski")
                .email("test@example.com")
                .city("Warszawa")
                .emailVerified(false)
                .build();
    }

    private Client createSavedClient() {
        return Client.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("test@example.com")
                .city("Warszawa")
                .emailVerified(false)
                .verificationToken("test-token")
                .build();
    }

    private Client createClientWithToken(String token) {
        return Client.builder()
                .id(1L)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("test@example.com")
                .city("Warszawa")
                .emailVerified(false)
                .verificationToken(token)
                .build();
    }

    private Client createVerifiedClient(Long id) {
        return Client.builder()
                .id(id)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("test@example.com")
                .city("Warszawa")
                .emailVerified(true)
                .build();
    }

    private Client createUnverifiedClient(Long id) {
        return Client.builder()
                .id(id)
                .firstName("Jan")
                .lastName("Kowalski")
                .email("test@example.com")
                .city("Warszawa")
                .emailVerified(false)
                .build();
    }

}