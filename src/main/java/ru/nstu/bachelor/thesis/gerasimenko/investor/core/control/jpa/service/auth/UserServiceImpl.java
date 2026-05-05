package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.exception.UserNotFoundException;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.repo.auth.UserRepository;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user) {
        return userRepository.save(user);
    }

    @Override
    public User login(User user) {
        user.setLastLoginAt(LocalDateTime.now());
        return updateLastActivity(user);
    }

    @Override
    public User updateLastActivity(User user) {
        user.setLastActivityAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() ->
                new UserNotFoundException(String.format("User not found: email=[%s]", email)));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UserNotFoundException {
        return findByEmail(email);
    }
}
