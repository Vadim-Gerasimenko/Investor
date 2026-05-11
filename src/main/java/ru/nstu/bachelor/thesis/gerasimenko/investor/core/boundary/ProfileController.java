package ru.nstu.bachelor.thesis.gerasimenko.investor.core.boundary;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.TBankUserDetailsService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.converter.UserConverter;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.UserProfileService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.AllTokensDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.TariffDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.TokenDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.UserProfileDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.NameRequestDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.tbank.TBankTokenDto;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

@RestController
@RequestMapping("api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserProfileService userProfileService;
    private final TBankUserDetailsService tBankUserDetailsService;

    @GetMapping("/user")
    public ResponseEntity<UserProfileDto> getUserDetails(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(UserConverter.convert(user));
    }

    @PutMapping("/user")
    public ResponseEntity<UserProfileDto> updateUserDetails(@AuthenticationPrincipal User user,
                                                            @RequestBody UserProfileDto profileDto) {
        userProfileService.update(user.getProfile(), profileDto);
        return ResponseEntity.ok().body(UserConverter.convert(user));
    }

    @GetMapping("/tariff")
    public ResponseEntity<TariffDto> getTariff(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(tBankUserDetailsService.getTariffInfo(user));
    }

    @GetMapping("/tokens")
    public ResponseEntity<AllTokensDto> getAllTokens(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok().body(tBankUserDetailsService.getAllTokensInfo(user));
    }

    @PostMapping("/tokens")
    public ResponseEntity<TokenDto> addToken(@AuthenticationPrincipal User user, @RequestBody TBankTokenDto tokenDto) {
        return ResponseEntity.ok().body(tBankUserDetailsService.addToken(user, tokenDto));
    }

    @DeleteMapping("/tokens")
    public ResponseEntity<?> removeToken(@AuthenticationPrincipal User user, @RequestBody NameRequestDto tokenNameDto) {
        tBankUserDetailsService.removeToken(user, tokenNameDto.name());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/tokens/activate")
    public ResponseEntity<TokenDto> activateToken(@AuthenticationPrincipal User user, @RequestBody NameRequestDto tokenNameDto) {
        return ResponseEntity.ok().body(tBankUserDetailsService.activateToken(user, tokenNameDto.name()));
    }
}