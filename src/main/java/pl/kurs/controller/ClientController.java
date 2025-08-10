package pl.kurs.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.ClientDto;
import pl.kurs.service.ClientService;

@RestController
@RequestMapping("/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClientDto createClient(@Validated @RequestBody ClientDto clientDto) {
        return clientService.registerClient(clientDto);
    }

    @GetMapping("/verification")
    public ResponseEntity<String> verifyEmail(@RequestParam String token) {
        if (clientService.verifyEmail(token)) {
            return ResponseEntity.ok("Email verified successfully!");
        } else {
            return ResponseEntity.badRequest().body("Invalid verification token.");
        }
    }

}
