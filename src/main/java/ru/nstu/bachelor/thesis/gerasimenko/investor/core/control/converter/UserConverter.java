package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter;

import lombok.experimental.UtilityClass;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.UserProfileDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

@UtilityClass
public class UserConverter {

    public static UserProfileDto convert(User user) {
        return UserProfileDto.builder()
                .email(user.getEmail())
                .phoneNumber(user.getProfile().getPhoneNumber())
                .firstName(user.getProfile().getFirstName())
                .middleName(user.getProfile().getMiddleName())
                .lastName(user.getProfile().getLastName())
                .birthDate(user.getProfile().getBirthDate())
                .avatarUrl(user.getProfile().getAvatarUrl())
                .build();
    }
}