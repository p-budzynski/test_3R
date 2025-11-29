package pl.kurs.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pl.kurs.dto.BookDto;
import pl.kurs.entity.Author;
import pl.kurs.entity.Book;
import pl.kurs.entity.Category;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.mapper.BookMapper;
import pl.kurs.repository.BookRepository;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookServiceTest {

    @Mock
    private BookRepository bookRepositoryMock;

    @Mock
    private CategoryService categoryServiceMock;

    @Mock
    private AuthorService authorServiceMock;

    @Mock
    private NotificationService notificationServiceMock;

    @Mock
    private BookMapper bookMapperMock;

    @InjectMocks
    private BookService bookService;

    @Test
    void shouldCreateBook() {
        //given
        BookDto expectedDto = new BookDto(1L, 1L, "Test title", 1L, 300);
        BookDto bookDto = new BookDto(null, 1L, "Test title", 1L, 300);
        Author author = new Author(1L, "Test author", null);
        Category category = new Category(1L, "Test category");
        Book book = new Book(null, "Test title", category, 300, author);
        Book savedBook = new Book(1L, "Test title", category, 300, author);

        when(categoryServiceMock.findById(1L)).thenReturn(category);
        when(authorServiceMock.findAuthorById(1L)).thenReturn(author);
        when(bookMapperMock.dtoToEntity(bookDto)).thenReturn(book);
        when(bookRepositoryMock.save(any(Book.class))).thenReturn(savedBook);
        when(bookMapperMock.entityToDto(savedBook)).thenReturn(expectedDto);
        doNothing().when(notificationServiceMock).publishCreatedBookNotification(book);

        //when
        BookDto result = bookService.createBook(bookDto);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getTitle()).isEqualTo("Test title");
        assertThat(result.getAuthorId()).isEqualTo(1L);
        assertThat(result.getCategoryId()).isEqualTo(1L);
        assertThat(result.getPageCount()).isEqualTo(300);
    }

    @Test
    void shouldReturnBookById() {
        //given
        Long bookId = 1L;
        Book book = new Book(bookId, "Test title", new Category(), 300, new Author());
        when(bookRepositoryMock.findById(bookId)).thenReturn(Optional.of(book));

        //when
        Book result = bookService.findBookById(bookId);

        //then
        assertThat(result).isEqualTo(book);
    }

    @Test
    void shouldThrowExceptionWhenBookNotFound() {
        //given
        Long bookId = 1L;
        when(bookRepositoryMock.findById(bookId))
                .thenReturn(Optional.empty());

        //when then
        assertThatThrownBy(() -> bookService.findBookById(bookId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage(STR."Book with id: \{bookId} not found");
    }

}