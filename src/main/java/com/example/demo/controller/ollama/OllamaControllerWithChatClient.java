package com.example.demo.controller.ollama;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.logging.Logger;


/*
        Note :-
        1. Earlier, we worked with ChatModel, which lets us interact with LLMs but has limited features.
            ● ChatClient is a higher-level, fluent API built on top of ChatModel.
            ● Provides more flexibility:
            ○ Send prompts and receive responses easily.
            ○ Responses can be single messages or streamed.
            ○ Access to more metadata (not just text).

        2. ChatClient is built on ChatModel
            ○ You still need a ChatModel instance to create it.
            ○ Example: ChatClient.create(chatModel).

        3. While the OllamaChatModel is used to create the ChatClient,
        the controller itself interacts with the ChatClient to handle API requests and responses.
        The ChatClient abstracts away the details of interacting with the Ollama API,
        allowing the controller to focus on handling HTTP requests and responses.
*/

@RestController
@RequestMapping("/api/ollama-chat-client")
public class OllamaControllerWithChatClient {

    Logger logger = Logger.getLogger(OllamaControllerWithChatClient.class.getName());
    // Inject the ChatClient to interact with the Ollama API
    private ChatClient chatClient;

    ChatMemory chatMemory = MessageWindowChatMemory.builder().build(); // This is an example of creating a ChatMemory instance using the MessageWindowChatMemory implementation. The MessageWindowChatMemory allows the ChatClient to maintain a memory of previous messages in the conversation, which can be useful for generating more contextually relevant responses from the Ollama API.



    // Constructor injection of the ChatClient using the OllamaChatModel to create the ChatClient
//    private OllamaControllerWithChatClient(OllamaChatModel chatModel) { // this is(i.e chat model) when we have multiple ChatModels and we want to inject the OllamaChatModel separately to create the ChatClient
//        // Create a ChatClient using the OllamaChatModel
//        this.chatClient = ChatClient.create(chatModel);
//    }

    // If we have only one ChatModel, we can directly inject the ChatClient without needing to inject the OllamaChatModel separately. This simplifies the constructor and allows us to create the ChatClient
    // using the builder pattern without needing to inject the OllamaChatModel separately.
    private OllamaControllerWithChatClient(ChatClient.Builder builder) {
//        this.chatClient = builder.build(); // This is the simplest way to create a ChatClient using the builder pattern without adding any advisors or customizations.

        this.chatClient = builder
                .defaultAdvisors(MessageChatMemoryAdvisor
                        .builder(chatMemory)
                        .build()) // This is an example of adding a default advisor to the ChatClient. The MessageChatMemoryAdvisor allows the ChatClient to maintain a memory of previous messages in the conversation, which can be useful for generating more contextually relevant responses from the Ollama API.
                .build();
    }

    @GetMapping("/test")
    public String test() {
        return "Ollama API is working!";
    }

//    @GetMapping("/response/{prompt}")
//    public ResponseEntity<String> getResponseFromOllamaChatClient(@PathVariable String prompt) {
//        // Call the ChatClient API using the chatClient and return the response
//        String response = chatClient.prompt(prompt)
//                .call()
//                .content();
//
//        return new ResponseEntity<>(response, HttpStatus.OK);
//    }

    @GetMapping("/response/{prompt}")
    public ResponseEntity<String> getResponseFromOllamaChatClient(@PathVariable String prompt) {
        // in this we are working with the ChatResponse object which contains more metadata about the response, not just the content.
        ChatResponse chatResponse = chatClient.prompt(prompt)
                .call()
                .chatResponse();

        if(chatResponse == null || chatResponse.getResult() == null || chatResponse.getResult().getOutput() == null) {
            return new ResponseEntity<>("No response from Ollama API", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        // Log the metadata information about the response
        String model = chatResponse.getMetadata().getModel();
        Long rateLimit = chatResponse.getMetadata().getRateLimit().getRequestsLimit();
        logger.info("Model used: " + model);
        logger.info("Rate limit: " + rateLimit);

        // Extract the actual response content from the ChatResponse object
        String response = chatResponse.getResult()
                .getOutput()
                .getText();

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
