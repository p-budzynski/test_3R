package pl.kurs.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import pl.kurs.dto.ClientDto;
import pl.kurs.entity.Client;
import pl.kurs.repository.ClientRepository;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Testcontainers
@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
public class ClientControllerTest {

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:4.1.2")
            .withExposedPorts(5672, 15672);

    @DynamicPropertySource
    static void configureRabbit(DynamicPropertyRegistry registry) {
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.username", rabbitMQContainer::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitMQContainer::getAdminPassword);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ClientRepository clientRepository;

    @Test
    void shouldCreateClient() throws Exception {
        //given
        ClientDto clientDto = new ClientDto();
        clientDto.setFirstName("Jan");
        clientDto.setLastName("Kowalski");
        clientDto.setEmail("jan.kowalski@example.com");
        clientDto.setEmailVerified(false);
        clientDto.setCity("Warszawa");

        //when then
        mockMvc.perform(post("/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(clientDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(header().string("Content-Type", "application/json"))
                .andExpect(jsonPath("$.firstName").value(clientDto.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(clientDto.getLastName()))
                .andExpect(jsonPath("$.email").value(clientDto.getEmail()))
                .andExpect(jsonPath("$.city").value(clientDto.getCity()));
    }

    @Test
    void shouldVerifyEmailSuccessfully() throws Exception {
        //given
        String testToken = "test-token-123";
        Client client = Client.builder()
                .firstName("Anna")
                .lastName("Nowak")
                .email("anna.nowak@example.com")
                .emailVerified(false)
                .city("London")
                .verificationToken(testToken)
                .build();

        clientRepository.save(client);

        //when then
        mockMvc.perform(get("/clients/verification")
                        .param("token", testToken))
                .andExpect(status().isOk())
                .andExpect(content().string("Email verified successfully!"));
    }

    @Test
    void shouldReturnBadRequestForInvalidToken() throws Exception {
        //when then
        mockMvc.perform(get("/clients/verification")
                        .param("token", "invalid-token"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid verification token."));
    }
}