package com.javarush.telegram;

import com.javarush.telegram.ChatGPTService;
import com.javarush.telegram.DialogMode;
import com.javarush.telegram.MultiSessionTelegramBot;
import com.javarush.telegram.UserInfo;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.Objects;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "SoulMatchAi_bot";
    public static final String TELEGRAM_BOT_TOKEN = "7036081339:AAH7IZd7oVDYPX_kcktFShs4XnvtTsvh4zg";
    public static final String OPEN_AI_TOKEN = "chat-gpt-token";

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {
        //TODO: основной функционал бота будем писать здесь
        String message = getMessageText();
        sendTextMessage("Привет");

        if (message.equals("/start")){
            sendPhotoMessage("avatar_main");
            return;
        }
        sendTextMessage("Вы написали: " + message);
        sendTextButtonsMessage("Выберите режим:", "Старт", "start", "Стоп", "stop");

    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
