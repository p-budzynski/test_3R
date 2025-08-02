package pl.kurs.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pl.kurs.exception.ResourceNotFoundException;

@AllArgsConstructor
@Getter
public enum SubscriptionType {
    CATEGORY("CATEGORY"),
    AUTHOR("AUTHOR");

    private final String value;

    public static SubscriptionType fromString(String subscriptionType) {
        if (subscriptionType == null || subscriptionType.trim().isEmpty()) {
            throw new ResourceNotFoundException("Subscription type cannot be empty.");
        }

        return switch (subscriptionType.trim().toUpperCase()) {
            case "CATEGORY" -> CATEGORY;
            case "AUTHOR" -> AUTHOR;
            default -> throw new ResourceNotFoundException("Unknown subscription type: " + subscriptionType);
        };
    }

    @Override
    public String toString() {
        return value;
    }
}