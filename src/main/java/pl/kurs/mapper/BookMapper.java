package pl.kurs.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import pl.kurs.dto.BookDto;
import pl.kurs.entity.Book;

@Mapper(componentModel = "spring")
public interface BookMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    Book dtoToEntity(BookDto dto);

    @Mapping(target = "categoryId", source = "category.id")
    BookDto entityToDto(Book entity);

}
