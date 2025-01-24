package com.javarush.telegram;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.ArrayList;
import java.util.List;

public class TinderBoltApp extends MultiSessionTelegramBot {
    public static final String TELEGRAM_BOT_NAME = "SoulMatchAi_bot";
    public static final String TELEGRAM_BOT_TOKEN = "7036081339:AAH7IZd7oVDYPX_kcktFShs4XnvtTsvh4zg";
    public static final String OPEN_AI_TOKEN = "Your_gpt_token";
    public ChatGPTService chatGPTService = new ChatGPTService(OPEN_AI_TOKEN);
    private DialogMode currentMode = null;
    private List<String> listMessages = new ArrayList<>();

    private UserInfo he;
    private UserInfo she;
    private int questionCount;

    public TinderBoltApp() {
        super(TELEGRAM_BOT_NAME, TELEGRAM_BOT_TOKEN);
    }

    @Override
    public void onUpdateEventReceived(Update update) {

        String message = getMessageText();

        if (message.equals("/start")) {
            currentMode = DialogMode.MAIN;
            sendPhotoMessage("main");
            String startMessage = loadMessage("main");
            sendTextMessage(startMessage);
            showMainMenu(" Главное меню бота", "/start",
                    "генерация Tinder - профля \uD83D\uDE0E", "/profile",
                    "сообщение для знакомства \uD83E\uDD70", "opener",
                    "переписка от вашего имени \uD83D\uDE08", "/message",
                    "переписка со звездами \uD83D\uDD25", "/date",
                    "Общение с ChatGPT \uD83E\uDDE0", "/gpt");
            return;
        }
        if (message.equals("/gpt")) {
            currentMode = DialogMode.GPT;
            sendPhotoMessage("gpt");
            String textGPT = loadMessage("gpt");
            sendTextMessage(textGPT);
            return;
        }
        if (currentMode == DialogMode.GPT) {
            String promt = loadPrompt("gpt");
            Message msg = sendTextMessage("Подождите пару секунд...");
            String answer = chatGPTService.sendMessage(promt, message);
            updateTextMessage(msg, answer);
            return;
        }
        if (message.equals("/date")) {
            currentMode = DialogMode.DATE;
            sendPhotoMessage("date");
            String textDate = loadMessage("date");
            sendTextButtonsMessage(textDate,
                    "Ариана Гранде", "date_grande",
                    "Марго Робби", "date_robbie",
                    "Зендая", "date_zendaya",
                    "Райан Гослинг", "date_gosling",
                    "Том Харди", "date_hardy");
            return;
        }
        if (currentMode == DialogMode.DATE && !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("date_")) {
                sendPhotoMessage(query);
                String promt = loadPrompt(query);
                chatGPTService.setPrompt(promt);
                return;
            }
            Message msg = sendTextMessage("Подождите пару секунд...");
            String answer = chatGPTService.addMessage(message);
            //System.out.println(message);
            updateTextMessage(msg, answer);
            return;
        }
        if (message.equals("/message")) {
            currentMode = DialogMode.MESSAGE;
            sendPhotoMessage("message");
            sendTextButtonsMessage("Пришлите в чат вашу переписку",
                    "Следующие сообщение", "message_next",
                    "Пригласить на свидание", "message_date");
            return;
        }
        if (currentMode == DialogMode.MESSAGE && !isMessageCommand()) {
            String query = getCallbackQueryButtonKey();
            if (query.startsWith("message_")) {
                String promt = loadPrompt(query);
                String userChatHistory = String.join("\n\n", listMessages);

                Message msg = sendTextMessage("Подождите пару секунд...");
                String answer = chatGPTService.sendMessage(promt, userChatHistory);
                updateTextMessage(msg, answer);
                return;
            }
            listMessages.add(message);
            System.out.println("Работает list");
            return;
        }
        if (message.equals("/profile")) {
            currentMode = DialogMode.PROFILE;
            sendPhotoMessage("profile");

            he = new UserInfo();
            questionCount = 1;

            sendTextMessage("Сколько вам лет ? ");
            return;
        }
        if (currentMode == DialogMode.PROFILE && !isMessageCommand()) {
            switch (questionCount) {
                case 1:
                    he.age = message;
                    questionCount++;
                    sendTextMessage("Кем вы работаете ?");
                    return;
                case 2:
                    he.occupation = message;
                    questionCount++;
                    sendTextMessage("У вас есть хобби ?");
                    return;
                case 3:
                    he.hobby = message;
                    questionCount++;
                    sendTextMessage("Что вам не нравится ? ");
                    return;
                case 4:
                    he.annoys = message;
                    questionCount++;
                    sendTextMessage("Цели знакомств ? ");
                    return;
                case 5:
                    he.goals = message;

                    String aboutMyself = he.toString();
                    String promt = loadPrompt("profile");
                    Message msg = sendTextMessage("Подождите пару секунд...");
                    String answer = chatGPTService.sendMessage(promt, aboutMyself);
                    updateTextMessage(msg, answer);
                    return;

            }
            return;
        }
        if (message.equals("/opener")) {
            currentMode = DialogMode.OPENER;
            sendPhotoMessage("opener");
            she = new UserInfo();
            questionCount = 1;
            sendTextMessage("Имя: ");
            return;
        }
        if (currentMode == DialogMode.OPENER && !isMessageCommand()) {
            switch (questionCount) {
                case 1:
                    she.name = message;
                    questionCount++;
                    sendTextMessage("Сколько лет ? ");
                    return;
                case 2:
                    she.age = message;
                    questionCount++;
                    sendTextMessage("Есть ли хобби ? ");
                    return;
                case 3:
                    she.hobby = message;
                    questionCount++;
                    sendTextMessage("Кем работает ? ");
                    return;
                case 4:
                    she.occupation = message;
                    questionCount++;
                    sendTextMessage("Цели ? ");
                    return;
                case 5:
                    she.goals = message;
                    questionCount++;
                    String aboutFriend = message;
                    String promt = loadPrompt("opener");
                    Message msg = sendTextMessage("Подождите пару секунд...");
                    String answer = chatGPTService.sendMessage(promt, aboutFriend);
                    updateTextMessage(msg, answer);
                    return;
            }
        }
    }

    public static void main(String[] args) throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new TinderBoltApp());
    }
}
