package net.tayebi.ebankbot.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.stereotype.Service;

@Service
public class EbankAIAgent {

    private final ChatClient chatClient;

    public EbankAIAgent(
            ChatClient.Builder chatClientBuilder,
            ToolCallbackProvider tools) {

        this.chatClient = chatClientBuilder
                .defaultSystem(
                        "Vous êtes un assistant chargé de répondre aux questions "
                                + "de l'utilisateur en fonction du contexte fourni à propos "
                                + "des clients et des comptes bancaires. "
                                + "Si aucun contexte n'est fourni, répondez : JE NE SAIS PAS."
                )
                .defaultTools(tools)
                .build();
    }

    public String chat(String query) {
        return chatClient
                .prompt()
                .user(query)
                .call()
                .content();
    }
}