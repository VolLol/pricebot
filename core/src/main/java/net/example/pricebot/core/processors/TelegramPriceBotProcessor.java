package net.example.pricebot.core.processors;

import net.example.pricebot.core.BotConfig;
import net.example.pricebot.core.CommandState;
import net.example.pricebot.core.dto.*;
import net.example.pricebot.core.usecases.*;
import net.example.pricebot.core.utils.KeyboardFactory;
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

public class TelegramPriceBotProcessor extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramPriceBotProcessor.class);
    private Map<Long, CommandState> chatState = new HashMap<>();
    private SqlSession session;

    public TelegramPriceBotProcessor(SqlSession session) {
        this.session = session;
    }

    private CommonAnswerEntity commonAnswerEntity;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage answer = new SendMessage();
        Long chatId = update.getMessage().getChatId();
        answer.setChatId(chatId);
        if (!chatState.containsKey(chatId)) {
            chatState.put(chatId, CommandState.NONE);
            ReplyKeyboardMarkup commandsButton = KeyboardFactory.generateKeyboard();
            answer.setReplyMarkup(commandsButton);
        }
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();

                if (!chatState.get(chatId).equals(CommandState.NONE)) {

                    if (chatState.get(chatId).equals(CommandState.ADD_WAIT_LINK)) {
                        scenarioAddRecord(text, chatId, session, answer);
                    }

                    if (chatState.get(chatId).equals(CommandState.DELETE_ALL_WAIT_ANSWER)) {
                        scenarioDeleteAll(text, chatId, answer, session);
                    }
                    if (chatState.get(chatId).equals(CommandState.SHOW_DIAGRAM_WAIT_PRODUCT_ID) &&
                            isNumeric(text)) {
                        scenarioCreateImage(text, String.valueOf(chatId), answer);
                        answer.setText("Show chart with good id = " + text);
                        chatState.put(chatId, CommandState.NONE);
                    }
                    sendMessage(answer);
                } else {
                    if (text.equals("/start") && chatState.get(chatId).equals(CommandState.NONE)) {
                        scenarioStart(answer);
                        logger.info("User " + update.getMessage().getChatId() + " starts work");
                    }
                    if (text.equals("/add") && chatState.get(chatId).equals(CommandState.NONE)) {
                        answer.setText("Please enter the link");
                        chatState.put(chatId, CommandState.ADD_WAIT_LINK);
                    }
                    if (text.equals("/showall") && chatState.get(chatId).equals(CommandState.NONE)) {
                        scenarioShowAllGoods(chatId, answer, session);
                    }
                    if (text.equals("/deleteall") && chatState.get(chatId).equals(CommandState.NONE)) {
                        answer.setText("Are you sure?[Yes/No]");
                        chatState.put(chatId, CommandState.DELETE_ALL_WAIT_ANSWER);
                    }
                    if (text.equals("/help") && chatState.get(chatId).equals(CommandState.NONE)) {
                        scenarioHelpCommand(answer);
                    }
                    if (text.equals("/showdiagram") && chatState.get(chatId).equals(CommandState.NONE)) {
                        answer.setText("Which product chart you want to see?");
                        chatState.put(chatId, CommandState.SHOW_DIAGRAM_WAIT_PRODUCT_ID);
                    }
                    if (!text.equals("/start")
                            && !text.equals("/add")
                            && !text.equals("/showall")
                            && !text.equals("/deleteall")
                            && !text.equals("/help")
                            && !text.equals("/showdiagram")
                    ) {
                        logger.info("User " + update.getMessage().getChat().getId() + " write incorrect command or information");
                        answer.setText("It is not a correct command or expected information");
                        chatState.put(chatId, CommandState.NONE);
                    }
                    sendMessage(answer);
                }
            }
        }
    }


    private void scenarioAddRecord(String text, Long chatId, SqlSession session, SendMessage answer) {
        try {
            commonAnswerEntity = AddRecordUsecase.execute(session, chatId, text);
            answer.setText(commonAnswerEntity.getMessageForUser());
            chatState.put(chatId, CommandState.NONE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void scenarioDeleteAll(String text, Long chatId, SendMessage answer, SqlSession session) {
        if (text.equalsIgnoreCase("yes") || text.equalsIgnoreCase("y")) {
            commonAnswerEntity = DeleteAllUsecase.execute(chatId, session);
            answer.setText(commonAnswerEntity.getMessageForUser());
        } else {
            answer.setText("The delete command has been canceled");
        }
        chatState.put(chatId, CommandState.NONE);
    }

    private void scenarioStart(SendMessage answer) {
        answer.enableHtml(true);
        StartUsecase startUsecase = new StartUsecase();
        commonAnswerEntity = startUsecase.execute();
        answer.setText(commonAnswerEntity.getMessageForUser());
    }

    private void scenarioShowAllGoods(Long chatId, SendMessage answer, SqlSession session) {
        commonAnswerEntity = ShowAllGoodsUsecase.execute(session, chatId.toString());
        answer.enableHtml(true);
        answer.setText(commonAnswerEntity.getMessageForUser());
    }


    private void scenarioHelpCommand(SendMessage answer) {
        answer.enableHtml(true);
        commonAnswerEntity = new HelpUsecase().execute();
        answer.setText(commonAnswerEntity.getMessageForUser());
    }

    private void scenarioCreateImage(String text, String chatId, SendMessage answer) {
        try {
            ShowDiagramUsecase showDiagramUsecase = new ShowDiagramUsecase(session);
            CreateImageAnswerEntity createImageAnswerEntity = showDiagramUsecase.execute(text);
            if (createImageAnswerEntity.getAnswerEnum().equals(AnswerEnum.SUCCESSFUL)) {
                SendPhoto sendPhoto = new SendPhoto();
                sendPhoto.setPhoto(createImageAnswerEntity.getImage());
                sendPhoto.setChatId(chatId);
                execute(sendPhoto);
            } else {
                answer.setText(createImageAnswerEntity.getMessageForUser());
                execute(answer);
            }
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private boolean isNumeric(String strNum) {
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


    private void sendMessage(SendMessage message) {
        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
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
