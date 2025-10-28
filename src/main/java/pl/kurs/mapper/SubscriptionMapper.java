package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Subscription;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "clientId", source = "client.id")
    SubscriptionDto entityToDto(Subscription entity);

    @Mapping(target = "client", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "category", ignore = true)
    Subscription dtoToEntity(SubscriptionDto dto);
}
