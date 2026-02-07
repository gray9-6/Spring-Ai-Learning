package com.example.demo.controller;

import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
    Note :-
    1. This controller is responsible for handling API requests related to the Ollama API.
    2. It uses the OllamaChatModel to interact with the Ollama API and return responses based on the prompts provided by the user.
 */
@RestController
@RequestMapping("/api/ollama-chat-model")
public class OllamaControllerWithChatModel {

    // Inject the OllamaChatModel to interact with the Ollama API
    private OllamaChatModel chatModel;

    // Constructor injection of the OllamaChatModel
    private OllamaControllerWithChatModel(OllamaChatModel chatModel) {
        this.chatModel = chatModel;
    }

    @GetMapping("/test")
    public String test() {
        return "Ollama API is working!";
    }

    @GetMapping("/response/{prompt}")
    public String getResponseFromOllamaChatModel(@PathVariable String prompt) {
        // Call the OllamaChatModel API using the chatModel and return the response
        String response = chatModel.call(prompt);
        return response;
    }


}
