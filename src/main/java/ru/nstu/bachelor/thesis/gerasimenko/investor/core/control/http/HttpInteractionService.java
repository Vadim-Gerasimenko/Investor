package ru.nstu.bachelor.thesis.gerasimenko.investor.core.control.http;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.nstu.bachelor.thesis.gerasimenko.investor.core.entity.dto.network.HttpHeaders;

@Slf4j
@Service
@RequiredArgsConstructor
public class HttpInteractionService {

    private final RestClient restClient;

    public <S> ResponseEntity<?> post(String uri, HttpHeaders headers, S body) {
        return post(uri, headers, body, Object.class);
    }

    public <S, D> ResponseEntity<D> post(String uri, HttpHeaders headers, S body, Class<D> responseType) {
        return ResponseEntity.ofNullable(addHeaders(restClient.post().uri(uri), headers)
                .body(body)
                .retrieve()
                .body(responseType));
    }

    private RestClient.RequestBodySpec addHeaders(RestClient.RequestBodySpec requestBodySpec, HttpHeaders headers) {
        headers.getHeaders().forEach(requestBodySpec::header);
        return requestBodySpec;
    }
}