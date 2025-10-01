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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class InvestmentService {

    private static final String API_URL = "http://localhost:8080/api/investments";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private static final Logger logger =  LoggerFactory.getLogger(InvestmentService.class);

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

    public List<InvestmentDTO> findAll() {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .header("Accept", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return List.of(objectMapper.readValue(response.body(), InvestmentDTO[].class));
            }
        } catch (Exception e) {
            logger.error("Failed to fetch investments from API", e);
        }
        return Collections.emptyList();
    }

    public String update(InvestmentDTO dto) {
        try {
            String body = objectMapper.writeValueAsString(dto);
            HttpRequest request = buildRequest()
                    .PUT(HttpRequest.BodyPublishers.ofString(body))
                    .uri(URI.create(API_URL + "/" + dto.id()))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                return "success";
            } else {
                return "Error updating: " + response.body();
            }
        } catch (IOException | InterruptedException ex) {
            return "Serialization/communication error: " + ex.getMessage();
        }
    }

    public String delete(String id) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL + "/" + id))
                    .DELETE()
                    .header("Accept", "application/json")
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200 || response.statusCode() == 204) {
                return "success";
            } else {
                return "Error deleting: " + response.body();
            }
        } catch (IOException | InterruptedException ex) {
            return "Communication error: " + ex.getMessage();
        }
    }

}