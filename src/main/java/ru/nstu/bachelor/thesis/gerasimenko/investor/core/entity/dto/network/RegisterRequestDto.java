package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.ToString;

public record RegisterRequestDto(
        @Email
        @NotBlank
        String email,

        @NotBlank
        @ToString.Exclude
        String password,

        @NotNull
        UserProfileDto profile) {
}