package net.tayebi.ebankbot.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Service
public class EbankAIAgent {
    private ChatClient chatClient;

    public EbankAIAgent(
            ChatClient.Builder chatClient,
            ChatMemory chatMemory,
            ToolCallbackProvider tools) {

        this.chatClient = chatClient
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(chatMemory).build()
                )
                .defaultSystem("Vous un assistant qui se charge de répondre aux question" +
                        " de l'utilisateur en fonction du contexte fourni a propos des clients  et des comptes bancaires ,si aucun contexte " +
                        "n'est fourni, répond avec JE NE SAIS PAS"
                )
                .defaultTools(tools)
                .build();
    }

    public String chat( String query) {

        return chatClient
                .prompt()
                .user(query)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        "user-1"
                ))
                .call()
                .content();
    }
}