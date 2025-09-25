package com.example.Personal_Finance_App.Service;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    // Example DB-backed values
    private Map<String, Double> categoryExpenses = Map.of(
        "Food", 7500.0,
        "Transport", 2000.0,
        "Rent", 12000.0
    );
    private double savingsGrowthRate = 12.5;

    public String getChatbotResponse(String question) {
        String normalized = question == null ? "" : question.toLowerCase().trim();

        if (normalized.contains("food last month")) {
            return "You spent ₹" + categoryExpenses.get("Food") + " on Food last month.";
        } else if (normalized.contains("savings growth")) {
            return "Your current savings growth rate is " + savingsGrowthRate + "%.";
        } else {
            return callGemini(question);
        }
    }

    // Optional helper: list models (useful to debug 404)
    public List<String> listAvailableModels() {
        try {
            String url = "https://generativelanguage.googleapis.com/v1beta/models?key=" + geminiApiKey;
            Map resp = restTemplate.getForObject(url, Map.class);
            List<String> result = new ArrayList<>();
            if (resp != null && resp.containsKey("models")) {
                List<Map<String,Object>> models = (List<Map<String,Object>>) resp.get("models");
                for (Map<String,Object> m : models) {
                    result.add((String) m.get("name"));
                }
            }
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return List.of("Error listing models: " + e.getMessage());
        }
    }

    private String callGemini(String question) {
        try {
            // Choose a valid model (or call listAvailableModels() and pick one)
            String model = "gemini-2.5-flash"; // change if your list shows a different name
            String url = "https://generativelanguage.googleapis.com/v1beta/models/"
                    + model + ":generateContent?key=" + geminiApiKey;

            // Build request body: { "contents": [ { "parts":[{"text":"..."}], "role":"user" } ] }
            Map<String, Object> part = Map.of("text", question);
            Map<String, Object> contentObj = Map.of(
                    "parts", List.of(part),
                    "role", "user"
            );
            Map<String, Object> body = Map.of("contents", List.of(contentObj));

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            Map response = restTemplate.postForObject(url, request, Map.class);

            if (response != null && response.containsKey("candidates")) {
                List<Map<String,Object>> candidates = (List<Map<String,Object>>) response.get("candidates");
                if (!candidates.isEmpty()) {
                    Map<String,Object> content = (Map<String,Object>) candidates.get(0).get("content");
                    List<Map<String,Object>> parts = (List<Map<String,Object>>) content.get("parts");
                    if (parts != null && !parts.isEmpty()) {
                        return (String) parts.get(0).get("text");
                    }
                }
            }
            return "No response from Gemini.";
        } catch (org.springframework.web.client.HttpClientErrorException e) {
            // Return helpful message for debugging (404, 401 etc.)
            return "Error calling Gemini: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error calling Gemini: " + e.getMessage();
        }
    }
}
