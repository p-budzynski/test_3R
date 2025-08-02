package pl.kurs.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import pl.kurs.validation.Delete;
import pl.kurs.validation.Update;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SubscriptionDto {
    @NotNull(message = "ID is required", groups = {Update.class, Delete.class})
    @Min(value = 1, message = "ID must be at least 1", groups = {Update.class, Delete.class})
    private Long id;

    @NotNull(message = "Client ID is required")
    @Min(value = 1, message = "Client ID must be at least 1")
    private Long clientId;

    @NotNull(message = "Subscription type is required")
    private String subscriptionType;

    @NotBlank(message = "Subscription value is required")
    private String subscriptionValue;

}
