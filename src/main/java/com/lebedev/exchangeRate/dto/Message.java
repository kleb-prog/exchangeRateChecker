package com.lebedev.exchangeRate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record Message(
        @NotBlank(message = "Message is empty")
        @Size(max = 4096, message = "Message must not exceed 4096 characters")
        String message) {
}
