package pl.kurs.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.kurs.entity.Author;

import static org.assertj.core.api.Assertions.assertThat;

public class AuthorMapperTest {
    private final AuthorMapper authorMapper = Mappers.getMapper(AuthorMapper.class);

    @Test
    void shouldReturnAuthorFromId() {
        //given
        Long authorId = 1L;

        //when
        Author result = authorMapper.authorFromId(authorId);

        //then
        assertThat(result.getId()).isEqualTo(authorId);
    }

    @Test
    void shouldReturnNullWhenIdIsNull() {
        //when
        Author result = authorMapper.authorFromId(null);

        //then
        assertThat(result).isNull();
    }
}