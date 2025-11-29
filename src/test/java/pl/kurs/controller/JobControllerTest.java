package pl.kurs.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.entity.*;
import pl.kurs.repository.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class JobControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private MessageConfigRepository messageConfigRepository;

    @Autowired
    private SubscriptionNotificationRepository notificationRepository;

    @Transactional
    @Test
    void shouldRunDailyNotificationJob() throws Exception {
        //given
        messageConfigRepository.save(new MessageConfig(null, "NEW_BOOKS", "TEST", "{{firstName}}\n{{bookList}}"));
        Client client1 = clientRepository.save(new Client(null, "Client1", "Client1", "c1@mail.com", "City1", true, null, null));
        Client client2 = clientRepository.save(new Client(null, "Client2", "Client2", "c2@mail.com", "City2", true, null, null));
        Author author = authorRepository.save(new Author(null, "Test Author", null));
        Category category = categoryRepository.save(new Category(null, "Test Category"));
        Book book = bookRepository.save(new Book(null, "Title Test", category, 100, author));

        notificationRepository.save(new SubscriptionNotification(null, client1, book));
        notificationRepository.save(new SubscriptionNotification(null, client2, book));

        //when them
        mockMvc.perform(post("/job/run"))
                .andExpect(status().isOk());
    }

}
