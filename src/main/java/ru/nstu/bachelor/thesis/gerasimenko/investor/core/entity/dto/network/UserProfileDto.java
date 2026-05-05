package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Builder
@Data
public class UserProfileDto {

    @NotBlank
    private String phoneNumber;

    @Email
    private String email;

    @NotBlank
    private String firstName;

    private String middleName;

    @NotBlank
    private String lastName;

    @JsonFormat(pattern = "dd.MM.yyyy")
    @NotBlank
    private LocalDate birthDate;

    private String avatarUrl;
}