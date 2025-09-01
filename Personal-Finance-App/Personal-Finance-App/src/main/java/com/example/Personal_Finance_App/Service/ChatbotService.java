package com.example.Personal_Finance_App.Service;


import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ChatbotService {

    // Example data: In real project, fetch from DB
    private Map<String, Double> categoryExpenses = Map.of(
            "Food", 7500.0,
            "Transport", 2000.0,
            "Rent", 12000.0
    );
    private double savingsGrowthRate = 12.5; // Example %

    public String getChatbotResponse(String question) {
        question = question.toLowerCase();

        if (question.contains("food last month")) {
            return "You spent ₹" + categoryExpenses.get("Food") + " on Food last month.";
        } else if (question.contains("savings growth")) {
            return "Your current savings growth rate is " + savingsGrowthRate + "%.";
        } else {
            // Call AI model (OpenAI / local ML)
            return callAIModel(question);
        }
    }

    private String callAIModel(String question) {
        // 🔗 Placeholder: integrate with OpenAI API or local ML
        return "I'm still learning. But here’s my guess: " + question;
    }
}
