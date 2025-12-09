package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.entity.Author;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.repository.AuthorRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorServiceTest {

    @Mock
    private AuthorRepository authorRepositoryMock;

    @InjectMocks
    private AuthorService authorService;

    @Test
    void shouldReturnAuthorById() {
        //given
        Author author = new Author(1L, "AuthorTest", null);

        when(authorRepositoryMock.findById(1L)).thenReturn(Optional.of(author));

        //when
        Author result = authorService.findAuthorById(1L);

        //then
        assertThat(result).isEqualTo(author);
    }

    @Test
    void shouldThrowExceptionWhenAuthorNotFound() {
        //given
        Long authorId = 1L;
        when(authorRepositoryMock.findById(authorId))
                .thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> authorService.findAuthorById(authorId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Author with id: " + authorId + " not found");
    }
}