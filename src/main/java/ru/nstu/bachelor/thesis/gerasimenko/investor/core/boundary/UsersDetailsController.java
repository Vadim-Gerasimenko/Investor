package ru.nstu.bachelor.thesis.gerasimenko.investor.core.boundary;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.UserConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.UserProfileDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

@RestController
@RequestMapping("api/users")
public class UsersDetailsController {

    @GetMapping
    public UserProfileDto getUser(@AuthenticationPrincipal User user) {
        return UserConverter.convert(user);
    }
}