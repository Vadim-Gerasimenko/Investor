package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.config.bean;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "investor-core.cors")
public class CorsProperties {

    @NotNull
    private List<String> allowedOrigins;
}