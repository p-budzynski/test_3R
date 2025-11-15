package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Author;
import pl.kurs.entity.Category;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;

@Mapper(componentModel = "spring")
public interface SubscriptionMapper {

    @Mapping(target = "clientId", source = "client.id")
    @Mapping(target = "authorId", source = "author.id")
    @Mapping(target = "categoryId", source = "category.id")
    SubscriptionDto entityToDto(Subscription entity);

    @Mapping(target = "client", expression = "java(clientFromId(dto.getClientId()))")
    @Mapping(target = "author", expression = "java(authorFromId(dto.getAuthorId()))")
    @Mapping(target = "category", expression = "java(categoryFromId(dto.getCategoryId()))")
    Subscription dtoToEntity(SubscriptionDto dto);

    default Client clientFromId(Long id) {
        if (id == null) return null;
        Client client = new Client();
        client.setId(id);
        return client;
    }

    default Author authorFromId(Long id) {
        if (id == null) return null;
        Author author = new Author();
        author.setId(id);
        return author;
    }

    default Category categoryFromId(Long id) {
        if (id == null) return null;
        Category category = new Category();
        category.setId(id);
        return category;
    }
}
