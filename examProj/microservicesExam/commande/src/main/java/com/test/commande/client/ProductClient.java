package com.test.commande.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class ProductClient {

    private final WebClient webClient;

    public ProductClient() {
        // Adjust baseUrl to point to the Produits service
        // e.g. http://localhost:8081 or the actual container name
        this.webClient = WebClient.builder()
                .baseUrl("http://localhost:8081")
                .build();
    }

    @CircuitBreaker(name = "produitCB", fallbackMethod = "fallbackGetPrice")
    public Double getProductPrice(Long productId) {
        String query = """
            query($id: ID!) {
              productById(id: $id) {
                price
              }
            }
        """;

        String body = String.format(
                "{\"query\":\"%s\",\"variables\":{\"id\":%d}}",
                query.replace("\n", "\\n"), productId
        );

        String response = webClient.post()
                .uri("/graphql")
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(String.class)
                .block();

        if (response != null && response.contains("\"price\":")) {
            String sub = response.substring(response.indexOf("\"price\":") + 8);
            StringBuilder sb = new StringBuilder();
            for (char c : sub.toCharArray()) {
                if ((c >= '0' && c <= '9') || c == '.') {
                    sb.append(c);
                } else {
                    break;
                }
            }
            return Double.parseDouble(sb.toString());
        }

        return 0.0;
    }

    // Fallback method if Produits is down or call fails
    public Double fallbackGetPrice(Long productId, Throwable t) {
        System.err.println("Fallback due to error: " + t.getMessage());
        // Return a default or cached price
        return 0.0;
    }
}
