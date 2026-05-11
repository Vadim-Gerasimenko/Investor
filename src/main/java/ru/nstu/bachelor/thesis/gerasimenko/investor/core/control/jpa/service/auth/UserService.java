package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.UserProfileDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.UserProfile;

public interface UserService extends UserDetailsService {

    User create(User user);

    User update(User user);

    User login(User user);

    User updateLastActivity(User user);

    User findByEmail(String email);

    boolean existsByEmail(String email);
}