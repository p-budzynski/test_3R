package pl.kurs.mapper;

import org.mapstruct.Mapper;
import pl.kurs.entity.Author;

@Mapper(componentModel = "spring")
public interface AuthorMapper {

    default Author authorFromId(Long id) {
        if (id == null) return null;
        Author author = new Author();
        author.setId(id);
        return author;
    }
}
