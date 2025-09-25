package com.example.Personal_Finance_App.Controller;

import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.Personal_Finance_App.Service.ChatbotService;

@RestController
@RequestMapping("/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @GetMapping("/query")
    @ResponseBody // important: returns plain text, not a view
    public String getResponse(@RequestParam String question, Principal principal) {
        String userName = "";
        if (principal != null) {
            userName = principal.getName(); // fetch email/username
        }

        // If it's the first message (like "hi" or "hello")
        String lowerQ = question.toLowerCase().trim();
        if (lowerQ.equals("hi") || lowerQ.equals("hello") || lowerQ.equals("hey")) {
            return "Hello, " + userName + "! 👋 How can I help you today?";
        }

        return chatbotService.getChatbotResponse(question);
    }

}