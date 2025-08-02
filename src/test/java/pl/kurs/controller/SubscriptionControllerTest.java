package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.entity.Client;
import pl.kurs.repository.ClientRepository;
import pl.kurs.service.SubscriptionService;

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

    @Test
    void shouldCreateSubscription() throws Exception {
        //given
        Client client = Client.builder()
                .firstName("Jan")
                .lastName("Nowak")
                .email("j.nowak@mail.com")
                .emailVerified(true)
                .city("London")
                .verificationToken(null)
                .build();
        Client savedClient = clientRepository.save(client);
        SubscriptionDto subscriptionDto = new SubscriptionDto();
        subscriptionDto.setClientId(savedClient.getId());
        subscriptionDto.setSubscriptionType("CATEGORY");
        subscriptionDto.setSubscriptionValue("Fantasy");

        //when then
        mockMvc.perform(post("/subscriptions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(subscriptionDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.clientId").value(subscriptionDto.getClientId()))
                .andExpect(jsonPath("$.subscriptionType").value(subscriptionDto.getSubscriptionType()))
                .andExpect(jsonPath("$.subscriptionValue").value(subscriptionDto.getSubscriptionValue()));
    }

    @Test
    void shouldCancelSubscription() throws Exception {
        //given
        Long id = 5L;

        //when then
        mockMvc.perform(delete("/subscriptions/{id}", id))
                .andExpect(status().isOk())
                .andExpect(content().string("Subscription cancelled"));
    }

}