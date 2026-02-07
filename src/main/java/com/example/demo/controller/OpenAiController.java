package com.example.demo.controller;

import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/openai")
public class OpenAiController {

    // Inject the OpenAiChatModel to interact with the OpenAI API
    private OpenAiChatModel chatModel;

    // Constructor injection of the OpenAiChatModel
    private OpenAiController(OpenAiChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/test")
    public String test() {
        return "OpenAI API is working!";
    }

    @GetMapping("/response/{prompt}")
    public String getResponseFromOpenAi(@PathVariable String prompt) {
        // Call the OpenAI API using the chatModel and return the response
        String response = chatModel.call(prompt);
        return response;
    }
}
