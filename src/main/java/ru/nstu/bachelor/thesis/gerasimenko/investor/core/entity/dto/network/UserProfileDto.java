package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
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