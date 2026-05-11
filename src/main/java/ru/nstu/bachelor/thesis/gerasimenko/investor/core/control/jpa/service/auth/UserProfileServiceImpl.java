package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.UserNotFoundException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.auth.UserProfileRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.UserProfileDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.UserProfile;

import java.time.LocalDate;

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
    public UserProfile update(UserProfile userProfile, UserProfileDto profileDto) {
        userProfile.setLastName(profileDto.getLastName());
        userProfile.setFirstName(profileDto.getFirstName());
        userProfile.setMiddleName(profileDto.getMiddleName());
        userProfile.setBirthDate(profileDto.getBirthDate());
        userProfile.setPhoneNumber(profileDto.getPhoneNumber());
        userProfile.setUpdatedAt(LocalDate.now());
        return userProfileRepository.save(userProfile);
    }
}
