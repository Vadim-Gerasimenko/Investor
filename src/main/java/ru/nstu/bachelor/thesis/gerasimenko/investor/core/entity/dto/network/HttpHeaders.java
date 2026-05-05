package ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network;

import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
@Builder
public class HttpHeaders {

    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    public static final String ACCEPT_HEADER = "Accept";
    public static final String AUTHORIZATION_HEADER = "Authorization";

    public static final String APPLICATION_JSON_VALUE = "application/json";

    public static final String BEARER_TOKEN = "Bearer ";

    private Map<String, String> headers;

    public static HttpHeaders restHeaders() {
        return new HttpHeadersBuilder()
                .header(CONTENT_TYPE_HEADER, APPLICATION_JSON_VALUE)
                .header(ACCEPT_HEADER, APPLICATION_JSON_VALUE)
                .build();
    }

    public HttpHeaders bearerToken(String token) {
        headers.put(AUTHORIZATION_HEADER, BEARER_TOKEN + token);
        return this;
    }

    public static class HttpHeadersBuilder {
        private Map<String, String> headers = new HashMap<>();

        public HttpHeadersBuilder header(String key, String value) {
            headers.put(key, value);
            return this;
        }
    }
}