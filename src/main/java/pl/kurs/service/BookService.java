package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.dto.BookDto;
import pl.kurs.entity.Author;
import pl.kurs.entity.Book;
import pl.kurs.entity.Category;
import pl.kurs.exception.ResourceNotFoundException;
import pl.kurs.mapper.BookMapper;
import pl.kurs.repository.BookRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final AuthorService authorService;
    private final NotificationService notificationService;
    private final BookMapper bookMapper;

    @Transactional
    public BookDto createBook(BookDto dto) {
        Category category = categoryService.findById(dto.getCategoryId());
        Author author = authorService.findAuthorById(dto.getAuthorId());
        Book book = bookMapper.dtoToEntity(dto);
        book.setCategory(category);
        book.setAuthor(author);
        Book savedBook = bookRepository.save(book);
        notificationService.publishCreatedBookNotification(book);

        return bookMapper.entityToDto(savedBook);
    }

    public Book findBookById(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(STR."Book with id: \{id} not found"));
    }
}
