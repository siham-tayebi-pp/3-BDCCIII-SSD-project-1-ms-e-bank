package net.tayebi.ebankbot.telegram;

import jakarta.annotation.PostConstruct;
import net.tayebi.ebankbot.controllers.EbankAIAgent;
import org.springframework.ai.content.Media;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.GetFile;
//import org.telegram.telegrambots.meta.api.objects.Media;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.token}")
    private String telegramBotToken;

    private final EbankAIAgent aiAgent;

    public TelegramBot(EbankAIAgent aiAgent) {
        this.aiAgent = aiAgent;
    }
    @Override
    public String getBotToken() {
        return telegramBotToken;
    }

    @Override
    public String getBotUsername() {
        return "e_bank_project1_bot";
    }

    @PostConstruct
    public void registerTelegramBot() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(this);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpdateReceived(Update telegramRequest) {

        try {

            if (!telegramRequest.hasMessage()) {
                return;
            }

            String messageText = telegramRequest.getMessage().getText();

            Long chatId = telegramRequest.getMessage().getChatId();

            List<PhotoSize> photos = telegramRequest.getMessage().getPhoto();

            List<Media> mediaList = new ArrayList<>();

            String caption = null;

            if (photos != null && !photos.isEmpty()) {

                caption = telegramRequest.getMessage().getCaption();

                if (caption == null) {
                    caption = "What do you see in this image?";
                }

                for (PhotoSize ps : photos) {

                    String fileId = ps.getFileId();

                    GetFile getFile = new GetFile();
                    getFile.setFileId(fileId);

                    // Traitement du fichier ici
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}