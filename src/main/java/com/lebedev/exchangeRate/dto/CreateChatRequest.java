package com.lebedev.exchangeRate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CreateChatRequest(
        @NotNull Long id,
        @NotBlank String firstName,
        @NotBlank String lastName) {
}
