package me.m41k0n.investment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import me.m41k0n.investment.dto.InvestmentDTO;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;

public class InvestmentService {

    private static final String API_URL = "http://localhost:8080/api/investments";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public InvestmentService() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    private HttpRequest.Builder buildRequest() {
        return HttpRequest.newBuilder()
                .uri(URI.create(API_URL))
                .header("Content-Type", "application/json");
    }

    public void save(InvestmentDTO dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);
            HttpRequest request = buildRequest()
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                    .thenApply(response -> response.statusCode() == 201
                            ? "success"
                            : "Error saving: " + response.body())
                    .exceptionally(ex -> "Communication error: " + ex.getMessage());
        } catch (IOException ex) {
            CompletableFuture.completedFuture("Serialization error: " + ex.getMessage());
        }
    }
}