package net.example.pricebot.core.processors;

import javafx.application.Platform;
import javafx.stage.Stage;
import net.example.pricebot.core.BotConfig;
import net.example.pricebot.core.CommandState;
import net.example.pricebot.core.dto.*;
import net.example.pricebot.core.usecases.AddRecordUsecase;
import net.example.pricebot.core.usecases.DeleteAllUsecase;
import net.example.pricebot.core.usecases.ShowAllGoodsUsecase;
import net.example.pricebot.core.usecases.ShowDiagramUsecase;
import net.example.pricebot.core.utils.CommandsRepository;
import net.example.pricebot.core.utils.KeyboardFactory;
import net.example.pricebot.store.records.GoodsInfoRecord;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TelegramPriceBotProcessor extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramPriceBotProcessor.class);
    private static Map<Long, CommandState> chatState = new HashMap<>();
    private static Boolean executeState = false;
    private SqlSession session;
    private Stage stage;

    public TelegramPriceBotProcessor(SqlSession session, Stage stage) {
        this.session = session;
        this.stage = stage;
    }

    private static CommonAnswerEntity commonAnswerEntity;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage answer = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        CommonAnswerEntity commonAnswerEntity;
        ReplyKeyboardMarkup commandsButton = KeyboardFactory.generateKeyboard();
        answer.setReplyMarkup(commandsButton);
        answer.setChatId(chatId);
        if (!chatState.containsKey(chatId)) {
            chatState.put(chatId, CommandState.NONE);
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                if (executeState) {
                    if (chatState.get(chatId).equals(CommandState.ADD_WAIT_LINK)) {
                        scenarioAddRecord(text, chatId, session, answer);
                    }

                    if (chatState.get(chatId).equals(CommandState.DELETE_ALL_WAIT_ANSWER)) {
                        scenarioDeleteAll(text, chatId, answer, session);
                    }
                    if (chatState.get(chatId).equals(CommandState.SHOW_DIAGRAM_WAIT_PRODUCT_ID) &&
                            isNumeric(text)) {

                        SendPhoto sendPhoto = new SendPhoto();
                        try {
                            CreateImageAnswerEntity createImageAnswerEntity = ShowDiagramUsecase.execute(session, stage, text);
                            logger.info("wait");
                            sendPhoto.setPhoto(createImageAnswerEntity.getImage());
                            sendPhoto.setChatId(chatId);
                            execute(sendPhoto);
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                        answer.setText("Show chart with good id = " + text);
                        chatState.put(chatId, CommandState.NONE);
                        executeState = false;

                    }
                } else {
                    if (CommandsRepository.getAllCommands().containsKey(text) && chatState.get(chatId).equals(CommandState.NONE)) {

                        if (text.equals("/start")) {
                            scenarioStart(answer);
                        }

                        if (text.equals("/add")) {
                            answer.setText("Please enter the link");
                            chatState.put(chatId, CommandState.ADD_WAIT_LINK);
                            executeState = true;
                        }
                        if (text.equals("/showall")) {
                            scenarioShowAllGoods(chatId, answer, session);
                        }
                        if (text.equals("/deleteall")) {
                            answer.setText("Are you sure?[Yes/No]");
                            chatState.put(chatId, CommandState.DELETE_ALL_WAIT_ANSWER);
                            executeState = true;
                        }
                        if (text.equals("/help")) {
                            scenarioHelpCommand(answer);
                        }
                        if (text.equals("/showdiagram")) {
                            answer.setText("Which product chart you want to see?");
                            executeState = true;
                            chatState.put(chatId, CommandState.SHOW_DIAGRAM_WAIT_PRODUCT_ID);
                        }
                    } else {
                        answer.setText("Its not a command");
                    }
                }
                try {
                    execute(answer);
                } catch (
                        TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private static void scenarioAddRecord(String text, Long chatId, SqlSession session, SendMessage answer) {
        {
            Pattern linkPattern = Pattern.compile("^https\\:\\/\\/www\\.avito\\.ru\\/.+");
            Matcher matcher = linkPattern.matcher(text);
            if (matcher.matches()) {
                try {
                    commonAnswerEntity = AddRecordUsecase.execute(session, chatId, text);
                    answer.setText(commonAnswerEntity.getMessageForUser());
                } catch (IOException e) {
                    logger.error("Exception was caught while adding a record to the database");
                    e.printStackTrace();
                }
            } else {
                logger.info("The user used an incorrect link");
                answer.setText("Can't use this link. Please write another ");
            }
            chatState.put(chatId, CommandState.NONE);
            executeState = false;
        }
    }

    private static void scenarioDeleteAll(String text, Long chatId, SendMessage answer, SqlSession session) {
        if (text.equalsIgnoreCase("yes") || text.equalsIgnoreCase("y")) {
            commonAnswerEntity = DeleteAllUsecase.execute(chatId, session);
            answer.setText(commonAnswerEntity.getMessageForUser());
        } else {
            answer.setText("The delete command has been canceled");
        }
        chatState.put(chatId, CommandState.NONE);
        executeState = false;
    }

    private static void scenarioStart(SendMessage answer) {
        answer.enableHtml(true);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Welcome to Avito ParserBot!\n" +
                "This bot understand following command:  \n\n");
        for (Map.Entry entry : CommandsRepository.getAllCommands().entrySet())
            stringBuilder.append("<b>" + entry.getKey() + "</b>" + " - " + entry.getValue() + "\n\n");
        answer.setText(stringBuilder.toString());

    }

    private static void scenarioShowAllGoods(Long chatId, SendMessage answer, SqlSession session) {
        try {
            ShowAllAnswerEntity goods = ShowAllGoodsUsecase.execute(session, chatId.toString());
            answer.enableHtml(true);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("<b>Id Title  Price</b>\n");
            for (GoodsInfoRecord record : goods.getAllRecords()) {
                stringBuilder.append("<b>" + record.getId() + "</b> " + record.getTitle() + " <b>" + record.getPrice() + " </b>\n");
            }
            answer.setText(stringBuilder.toString());
        } catch (NullPointerException e) {
            answer.setText("You are not watching any goods");
        }
    }


    private static void scenarioHelpCommand(SendMessage answer) {
        answer.enableHtml(true);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("This bot understand following command:  \n\n");
        for (Map.Entry entry : CommandsRepository.getAllCommands().entrySet())
            stringBuilder.append("<b>" + entry.getKey() + "</b>" + " - " + entry.getValue() + "\n\n");
        answer.setText(stringBuilder.toString());
    }


    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    @Override
    public String getBotUsername() {
        return BotConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return BotConfig.TOKEN;
    }
}
