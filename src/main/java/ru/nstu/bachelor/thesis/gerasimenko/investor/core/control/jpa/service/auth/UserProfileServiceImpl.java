package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.UserNotFoundException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.auth.UserProfileRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.UserProfile;

@Service
@RequiredArgsConstructor
public class UserProfileServiceImpl implements UserProfileService {

    private final UserProfileRepository userProfileRepository;

    @Override
    public UserProfile get(Long userId) {
        return userProfileRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException(String.format("User profile not found: userId=[%s]", userId)));
    }

    @Override
    public UserProfile create(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }

    @Override
    public UserProfile update(UserProfile userProfile) {
        return userProfileRepository.save(userProfile);
    }
}
