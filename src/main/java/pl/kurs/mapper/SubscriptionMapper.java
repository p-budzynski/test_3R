package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Subscription;

@Mapper(componentModel = "spring", uses = {AuthorMapper.class, CategoryMapper.class, ClientMapper.class})
public interface SubscriptionMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    SubscriptionDto entityToDto(Subscription entity);

    @Mapping(target = "client", source = "clientId")
    @Mapping(target = "author", source = "authorId")
    @Mapping(target = "category", source = "categoryId")
    Subscription dtoToEntity(SubscriptionDto dto);

}
