package ru.nstu.bachelor.thesis.gerasimenko.investor.core.boundary.tbank;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.jpa.service.auth.TBankTokenService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.router.Router;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http.HttpInteractionService;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.utils.HttpUtils;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.HttpHeaders;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.jpa.auth.User;

@Slf4j
@RestController
@RequestMapping("api/dev/services/instruments")
@ConditionalOnProperty(prefix = "investor-core", name = "dev-mode", havingValue = "true")
public class InstrumentsController {

    private final HttpInteractionService httpService;
    private final TBankTokenService tBankTokenService;
    private final String baseUrl;

    public InstrumentsController(HttpInteractionService httpService,
                                 TBankTokenService tBankTokenService,
                                 Router router) {
        this.httpService = httpService;
        this.tBankTokenService = tBankTokenService;
        this.baseUrl = router.getRouteToInstruments();
    }

    @PostMapping("/{endpoint}")
    public ResponseEntity<?> post(@AuthenticationPrincipal User user,
                                  @PathVariable String endpoint,
                                  @RequestBody String body) {
        log.info("Received request to post instruments service endpoint: {}", endpoint);
        String url = HttpUtils.getUrl(baseUrl, endpoint);
        log.info("Generated url to route: {}", url);

        HttpHeaders headers = HttpHeaders.restHeaders().bearerToken(tBankTokenService.getActiveToken(user).getToken());
        return ResponseEntity.ok(httpService.post(url, headers, body));
    }
}