package pl.kurs.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import pl.kurs.dto.BookDto;
import pl.kurs.entity.Book;
import pl.kurs.entity.Category;
import pl.kurs.event.BookCreatedEvent;
import pl.kurs.mapper.BookMapper;
import pl.kurs.repository.BookRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final CategoryService categoryService;
    private final BookMapper bookMapper;
    private final ApplicationEventPublisher applicationEventPublisher;

    public BookDto createBook(BookDto dto) {
        Category category = categoryService.findById(dto.getCategoryId());

        Book book = bookMapper.dtoToEntity(dto);
        book.setCategory(category);
        Book savedBook = bookRepository.save(book);

        applicationEventPublisher.publishEvent(new BookCreatedEvent(this, savedBook.getId()));

        return bookMapper.entityToDto(savedBook);
    }

    public Optional<Book> findById(Long bookId) {
        return bookRepository.findById(bookId);
    }
}
