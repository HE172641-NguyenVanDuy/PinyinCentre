package com.pinyincentre.pinyin.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URI;
import java.util.Map;

@Slf4j
@Component
public class ApiClient {

    private final WebClient webClient;

    public ApiClient(WebClient webClient) {
        this.webClient = webClient;
    }

    public <T> T postV2(String url, Object body, Class<T> responseType, Map<String, String> headers) {
        try {
            WebClient.RequestBodySpec spec = webClient.post()
                    .uri(URI.create(url))
                    .contentType(MediaType.APPLICATION_JSON);

            if (headers != null) {
                headers.forEach(spec::header);
            }

            return spec
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (Exception e) {
            log.error("Error calling POST {}: {}", url, e.getMessage());
            throw new RuntimeException("Error calling API: " + e.getMessage(), e);
        }
    }

    // ======= Generic POST =======
    public <T> T post(String url, Object body, Class<T> responseType, Map<String, String> headers) {
        try {
            WebClient.RequestBodySpec spec = webClient.post()
                    .uri(url)
                    .contentType(MediaType.APPLICATION_JSON);

            if (headers != null) {
                headers.forEach(spec::header);
            }

            return spec
                    .bodyValue(body)
                    .retrieve()
                    .bodyToMono(responseType)
                    .block();
        } catch (Exception e) {
            log.error("Error calling POST {}: {}", url, e.getMessage());
            throw new RuntimeException("Error calling API: " + e.getMessage(), e);
        }
    }

    // ======= Generic GET =======
    public <T> T get(String url, Class<T> responseType, Map<String, String> headers) {
        try {
            WebClient.RequestHeadersSpec<?> spec = webClient.get().uri(url);

            if (headers != null) {
                spec = spec.headers(httpHeaders -> headers.forEach(httpHeaders::add));
            }

            return spec.retrieve().bodyToMono(responseType).block();
        } catch (Exception e) {
            log.error("Error calling GET {}: {}", url, e.getMessage());
            throw new RuntimeException("Error calling API: " + e.getMessage(), e);
        }
    }

    // ======= Generic DELETE =======
    public String delete(String url, Map<String, String> headers) {
        try {
            WebClient.RequestHeadersSpec<?> spec = webClient.delete().uri(url);
            if (headers != null) {
                spec = spec.headers(httpHeaders -> headers.forEach(httpHeaders::add));
            }
            return spec.retrieve().bodyToMono(String.class).block();
        } catch (Exception e) {
            log.error("Error calling DELETE {}: {}", url, e.getMessage());
            throw new RuntimeException("Error calling API: " + e.getMessage(), e);
        }
    }
}