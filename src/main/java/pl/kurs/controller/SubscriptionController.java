package pl.kurs.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import pl.kurs.dto.SubscriptionDto;
import pl.kurs.service.SubscriptionService;

@RestController
@RequestMapping("/subscriptions")
@RequiredArgsConstructor
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto createSubscription(@Validated @RequestBody SubscriptionDto subscriptionDto) {
            return subscriptionService.createSubscription(subscriptionDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> cancelSubscriptionById(
            @PathVariable("id") @Min(value = 1, message = "ID must be greater than zero!") Long id) {
        subscriptionService.cancelSubscriptionById(id);
        return ResponseEntity.ok("Subscription cancelled");
    }
}
