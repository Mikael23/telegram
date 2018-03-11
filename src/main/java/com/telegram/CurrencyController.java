package com.telegram;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.DefaultAsyncHttpClient;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toMap;

public class CurrencyController {

    private static final String URL = "https://www.cbr-xml-daily.ru/daily_json.js";

    private final AsyncHttpClient httpClient = new DefaultAsyncHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    public CompletableFuture<Map<String, Double>> getCurrencies() {
        return httpClient.prepareGet(URL).execute().toCompletableFuture().thenApply(resp -> {
            try {
                String body = resp.getResponseBody();
                ServiceResponse serviceResponse = mapper.readValue(body, ServiceResponse.class);
                return serviceResponse.currencies.entrySet().stream()
                        .collect(toMap(Map.Entry::getKey, e -> e.getValue().value));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public static void main(String[] args) {
        CurrencyController controller = new CurrencyController();
        controller.getCurrencies();
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class ServiceResponse {
        private final Map<String, CurrencyDescription> currencies;

        @JsonCreator
        private ServiceResponse(@JsonProperty("Valute") Map<String, CurrencyDescription> currencies) {
            this.currencies = currencies;
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class CurrencyDescription {
        private final double value;

        @JsonCreator
        private CurrencyDescription(@JsonProperty("Value") double value) {
            this.value = value;
        }
    }

}
