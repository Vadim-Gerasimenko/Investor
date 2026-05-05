package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.ToString;

public record LoginRequestDto(

        @Email
        @NotBlank
        String email,

        @ToString.Exclude
        @NotBlank
        String password) {
}