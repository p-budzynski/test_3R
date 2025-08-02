package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.ClientDto;
import pl.kurs.entity.Client;

import static org.assertj.core.api.Assertions.assertThat;

public class ClientMapperTest {
    private final ClientMapper clientMapper = Mappers.getMapper(ClientMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        Client testClient = createTestClient();
        ClientDto testClientDto = createTestClientDto();

        //when
        ClientDto dto = clientMapper.entityToDto(testClient);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testClientDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //given
        Client testClient = createTestClient();
        ClientDto testClientDto = createTestClientDto();

        //when
        Client entity = clientMapper.dtoToEntity(testClientDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("id", "verificationToken", "emailVerified", "subscriptions")
                .isEqualTo(testClient);
    }

    @Test
    void shouldReturnNullWhenEntityToDtoGivenNull() {
        //when then
        assertThat(clientMapper.entityToDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoToEntityGivenNull() {
        //when then
        assertThat(clientMapper.dtoToEntity(null)).isNull();
    }

    private Client createTestClient() {
        return new Client(1L, "John", "Cena", "j.cena@mail.com", "Tampa", true, null, null);
    }

    private ClientDto createTestClientDto() {
        return new ClientDto(1L, "John", "Cena","j.cena@mail.com", true, "Tampa");
    }

}