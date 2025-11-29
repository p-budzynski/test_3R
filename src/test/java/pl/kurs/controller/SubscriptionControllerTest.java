package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Category;
import pl.kurs.entity.Client;
import pl.kurs.entity.Subscription;
import pl.kurs.repository.CategoryRepository;
import pl.kurs.repository.ClientRepository;
import pl.kurs.repository.SubscriptionRepository;
import pl.kurs.service.SubscriptionService;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class SubscriptionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SubscriptionService subscriptionService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Test
    void shouldCreateSubscription() throws Exception {
        //given
        Client client = clientRepository.save(new Client(null, "Test name", "Test name", "test@mail.com", "Test city", true, null, null));
        Category category = categoryRepository.save(new Category(null, "Test Category"));

        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setClientId(client.getId());
        subscriptionDto.setCategoryId(category.getId());

        //when then
        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").value(subscriptionDto.getClientId()))
                .andExpect(jsonPath("$.categoryId").value(subscriptionDto.getCategoryId()));
    }

    @Test
    void shouldCancelSubscription() throws Exception {
        //given
        Client client = clientRepository.save(new Client(null, "Test name", "Test name", "test@mail.com", "Test city", true, null, null));
        Category category = categoryRepository.save(new Category(null, "Test Category"));

        Subscription subscription = new Subscription(null, client, null,category);
        subscription = subscriptionRepository.save(subscription);

        //when then
        mockMvc.perform(delete("/subscriptions/{id}", subscription.getId()))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscription cancelled"));

        assertThat(subscriptionRepository.findById(subscription.getId())).isEmpty();
    }

}