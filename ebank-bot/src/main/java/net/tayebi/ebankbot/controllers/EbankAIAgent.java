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
import reactor.core.publisher.Flux;

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
                .defaultSystem("""
    Tu es un assistant intelligent spécialisé dans la gestion
    des clients et des comptes bancaires.

    Règles :
    1. Pour répondre aux questions concernant les clients et les comptes,
       utilise les outils disponibles lorsque cela est nécessaire.
    2. Ne dis pas que tu ne connais pas la réponse avant d'avoir
       vérifié les informations disponibles via les outils.
    3. Réponds uniquement à partir des informations réellement obtenues
       depuis les outils ou fournies dans la conversation.
    4. Si les informations nécessaires sont introuvables ou si les outils
       ne permettent pas de les obtenir, réponds exactement :
       JE NE SAIS PAS
    5. N'invente jamais de client, de compte, de solde ou d'identifiant.
    """)
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
    public Flux<String> chatStream(String query) {

        return chatClient
                .prompt()
                .user(query)
                .advisors(a -> a.param(
                        ChatMemory.CONVERSATION_ID,
                        "user-1"
                ))
                .stream()
                .content();
    }
}