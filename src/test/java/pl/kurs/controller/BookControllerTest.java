package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.dto.BookDto;
import pl.kurs.entity.Author;
import pl.kurs.entity.Category;
import pl.kurs.repository.AuthorRepository;
import pl.kurs.repository.BookRepository;
import pl.kurs.repository.CategoryRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Long authorId;
    private Long categoryId;

    @BeforeEach
    void before() {
        authorRepository.deleteAll();
        categoryRepository.deleteAll();
        bookRepository.deleteAll();

        Author savedAuthor = authorRepository.save(new Author(null, "Test Author", null));
        authorId = savedAuthor.getId();

        Category savedCategory = categoryRepository.save(new Category(null, "Test Category"));
        categoryId = savedCategory.getId();
    }

    @Test
    void shouldCreateBook() throws Exception {
        //given
        BookDto bookDto = new BookDto();
        bookDto.setAuthorId(authorId);
        bookDto.setTitle("Title Test");
        bookDto.setCategoryId(categoryId);
        bookDto.setPageCount(100);

        //when then
        mockMvc.perform(post("/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.authorId").value(bookDto.getAuthorId()))
                .andExpect(jsonPath("$.title").value(bookDto.getTitle()))
                .andExpect(jsonPath("$.categoryId").value(bookDto.getCategoryId()))
                .andExpect(jsonPath("$.pageCount").value(bookDto.getPageCount()));
    }

}