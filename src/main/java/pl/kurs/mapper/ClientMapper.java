package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.ClientDto;
import pl.kurs.entity.Client;

@Mapper(componentModel = "spring")
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "verificationToken", ignore = true)
    @Mapping(target = "subscriptions", ignore = true)
    Client dtoToEntity(ClientDto clientDto);

    ClientDto entityToDto(Client entity);

    default Client clientFromId(Long id) {
        if (id == null) return null;
        Client client = new Client();
        client.setId(id);
        return client;
    }
}
