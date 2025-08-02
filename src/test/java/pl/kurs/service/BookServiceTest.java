package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import pl.kurs.dto.BookDto;
import pl.kurs.entity.Book;
import pl.kurs.entity.Category;
import pl.kurs.mapper.BookMapper;
import pl.kurs.repository.BookRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepositoryMock;

    @Mock
    private CategoryService categoryServiceMock;

    @Mock
    private BookMapper bookMapperMock;

    @Mock
    private ApplicationEventPublisher applicationEventPublisherMock;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldCreateBook() {
        //given
        BookDto expectedDto = new BookDto(2L, "Test author", "Test title", 1L, 300);
        BookDto bookDto = new BookDto(null, "Test author", "Test title", 1L, 300);
        Category category = new Category(1L, "Fantasy");
        Book book = new Book(null, "Test author", "Test title", null, 300);
        Book savedBook = new Book(2L, "Test author", "Test title", category, 300);

        when(categoryServiceMock.findById(1L)).thenReturn(category);
        when(bookMapperMock.dtoToEntity(bookDto)).thenReturn(book);
        when(bookRepositoryMock.save(any(Book.class))).thenReturn(savedBook);
        when(bookMapperMock.entityToDto(savedBook)).thenReturn(expectedDto);

        //when
        BookDto result = bookService.createBook(bookDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(2L);
        assertThat(result.getTitle()).isEqualTo("Test title");
        assertThat(result.getAuthor()).isEqualTo("Test author");
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getPageCount()).isEqualTo(300);
    }

    @Test
    void shouldFindBookById() {
        // given
        Book savedBook = new Book(2L, "Test author", "Test title", new Category(), 300);
        Long bookId = 2L;
        when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(savedBook));

        //when
        Optional<Book> result = bookService.findById(bookId);

        //then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(savedBook);
        assertThat(result.get().getId()).isEqualTo(2L);
        assertThat(result.get().getTitle()).isEqualTo("Test title");
        assertThat(result.get().getAuthor()).isEqualTo("Test author");
    }

}