package net.tayebi.ebankbot.discord;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.tayebi.ebankbot.controllers.EbankAIAgent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class DiscordBot extends ListenerAdapter {

    private final EbankAIAgent aiAgent;

    @Value("${discord.token}")
    private String token;

    public DiscordBot(EbankAIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }

    @jakarta.annotation.PostConstruct
    public void startBot() {
        try {
            JDA jda = JDABuilder.createDefault(
                            token,
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.MESSAGE_CONTENT
                    )
                    .addEventListeners(this)
                    .build();

            jda.awaitReady();

            System.out.println("=================================");
            System.out.println("DISCORD BOT CONNECTÉ !");
            System.out.println("Nom : " + jda.getSelfUser().getName());
            System.out.println("ID  : " + jda.getSelfUser().getId());
            System.out.println("Serveurs : " + jda.getGuilds().size());
            System.out.println("=================================");

            jda.awaitReady();

            System.out.println("=================================");
            System.out.println("DISCORD BOT CONNECTÉ !");
            System.out.println("Nom : " + jda.getSelfUser().getName());
            System.out.println("ID  : " + jda.getSelfUser().getId());

            System.out.println("SERVEURS :");
            jda.getGuilds().forEach(guild ->
                    System.out.println("- " + guild.getName() + " | " + guild.getId())
            );

            System.out.println("=================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {

        System.out.println("🔥 EVENT DISCORD REÇU !");

        System.out.println("Serveur : " + event.getGuild().getName());
        System.out.println("Salon   : " + event.getChannel().getName());
        System.out.println("Auteur  : " + event.getAuthor().getName());
        System.out.println("Message : " + event.getMessage().getContentRaw());

        if (event.getAuthor().isBot()) {
            return;
        }

        event.getChannel()
                .sendMessage("✅ J'ai reçu ton message !")
                .queue();
    }
}