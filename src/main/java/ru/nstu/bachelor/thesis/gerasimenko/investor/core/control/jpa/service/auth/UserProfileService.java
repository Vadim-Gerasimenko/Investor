package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.UserProfile;

public interface UserProfileService {

    UserProfile get(Long userId);

    UserProfile create(UserProfile userProfile);

    UserProfile update(UserProfile userProfile);
}