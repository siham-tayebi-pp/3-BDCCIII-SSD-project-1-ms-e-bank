package net.tayebi.ebankbot.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class EbankChatbotController {

    private EbankAIAgent ebankAIAgent;
    public EbankChatbotController(EbankAIAgent ebankAIAgent) {
        this.ebankAIAgent = ebankAIAgent;
    }


    @GetMapping(value = "/chat", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chat(
            @RequestParam(name = "query", defaultValue = "Bonjour") String query) {

        return ebankAIAgent.chat(query);
    }
    @GetMapping(value = "/chat-streaming", produces = MediaType.TEXT_PLAIN_VALUE)
    public String chatStreaming(
            @RequestParam(name = "query", defaultValue = "Bonjour") String query) {

        return ebankAIAgent.chat(query);
    }
}