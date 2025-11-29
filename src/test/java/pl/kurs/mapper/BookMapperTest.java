package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.dto.BookDto;
import pl.kurs.entity.Author;
import pl.kurs.entity.Book;
import pl.kurs.entity.Category;

import static org.assertj.core.api.Assertions.assertThat;

public class BookMapperTest {
    private final BookMapper bookMapper = Mappers.getMapper(BookMapper.class);

    @Test
    void shouldMapEntityToDto() {
        //given
        Book testBook = createTestBook();
        BookDto testBookDto = createTestBookDto();

        //when
        BookDto dto = bookMapper.entityToDto(testBook);

        //then
        assertThat(dto)
                .usingRecursiveComparison()
                .isEqualTo(testBookDto);
    }

    @Test
    void shouldMapDtoToEntity() {
        //given
        Book testBook = createTestBook();
        BookDto testBookDto = createTestBookDto();

        //when
        Book entity = bookMapper.dtoToEntity(testBookDto);

        //then
        assertThat(entity)
                .usingRecursiveComparison()
                .ignoringFields("id", "category", "author")
                .isEqualTo(testBook);
    }

    @Test
    void shouldReturnNullWhenEntityToDtoGivenNull() {
        //when then
        assertThat(bookMapper.entityToDto(null)).isNull();
    }

    @Test
    void shouldReturnNullWhenDtoToEntityGivenNull() {
        //when then
        assertThat(bookMapper.dtoToEntity(null)).isNull();
    }

    @Test
    void shouldReturnNullCategoryIdsWhenFieldsAreNull() {
        //given
        Book testBook = createTestBook();
        testBook.setCategory(null);

        //when
        BookDto dto = bookMapper.entityToDto(testBook);

        //then
        assertThat(dto.getAuthorId()).isEqualTo(testBook.getAuthor().getId());
        assertThat(dto.getTitle()).isEqualTo(testBook.getTitle());
        assertThat(dto.getCategoryId()).isNull();
        assertThat(dto.getPageCount()).isEqualTo(testBook.getPageCount());
    }

    @Test
    void shouldReturnNullAuthorIdsWhenFieldsAreNull() {
        //given
        Book testBook = createTestBook();
        testBook.setAuthor(null);

        //when
        BookDto dto = bookMapper.entityToDto(testBook);

        //then
        assertThat(dto.getAuthorId()).isNull();
        assertThat(dto.getTitle()).isEqualTo(testBook.getTitle());
        assertThat(dto.getCategoryId()).isEqualTo(testBook.getCategory().getId());
        assertThat(dto.getPageCount()).isEqualTo(testBook.getPageCount());
    }

    private Book createTestBook() {
        return new Book(1L,"Rok 1984", new Category(1L,"Science Fiction"),328, new Author(1L, "George Orwell",null));
    }

    private BookDto createTestBookDto() {
        return new BookDto(1L,1L,"Rok 1984",1L,328);
    }


}