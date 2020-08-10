package net.example.pricebot.core.processors;

import net.example.pricebot.core.BotConfig;
import net.example.pricebot.core.answerEntityes.*;
import net.example.pricebot.core.usecases.*;
import net.example.pricebot.core.utils.KeyboardFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

public class TelegramPriceBotProcessor extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramPriceBotProcessor.class);
    private Map<Long, ProcessorState> chatStateMap = new HashMap<>();
    private CommonAnswerEntity commonAnswerEntity;

    @Override
    public void onUpdateReceived(Update update) {
        SendMessage answer = new SendMessage();
        Long chatId;
        if (update.hasCallbackQuery()) {
            chatId = Long.valueOf(update.getCallbackQuery().getFrom().getId());
            isItFirstTime(chatId, answer);
            if (update.getCallbackQuery().getData().equals("yes") &&
                    chatStateMap.get(chatId).equals(ProcessorState.DELETE_ALL_WAIT_ANSWER)) {
                commonAnswerEntity = new DeleteAllUsecase().execute(chatId);
                answer.setText(commonAnswerEntity.getMessageForUser());
            } else {
                answer.setText("The delete command has been canceled");
            }
            chatStateMap.put(chatId, ProcessorState.NONE);
            answer.setChatId(chatId);
            sendMessage(answer);
        } else {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                chatId = update.getMessage().getChatId();
                isItFirstTime(chatId, answer);
                answer.setChatId(chatId);
                if (message.hasText()) {
                    String text = message.getText();

                    if (!chatStateMap.get(chatId).equals(ProcessorState.NONE)) {

                        if (chatStateMap.get(chatId).equals(ProcessorState.ADD_WAIT_LINK)) {
                            scenarioAddRecord(text, chatId, answer);
                        }


                        if (chatStateMap.get(chatId).equals(ProcessorState.SHOW_DIAGRAM_WAIT_PRODUCT_ID) &&
                                isNumeric(text)) {
                            scenarioCreateImage(text, String.valueOf(chatId), answer);
                            answer.setText("Show chart with good id = " + text);
                            chatStateMap.put(chatId, ProcessorState.NONE);
                        }
                        sendMessage(answer);
                    } else {
                        if (text.equals("/start")) {
                            scenarioStart(answer);
                            logger.info("User " + update.getMessage().getChatId() + " starts work");
                        }
                        if (text.equals("/add")) {
                            answer.setText("Please enter the link");
                            chatStateMap.put(chatId, ProcessorState.ADD_WAIT_LINK);
                        }
                        if (text.equals("/showall")) {
                            scenarioShowAllGoods(chatId, answer);
                        }
                        if (text.equals("/deleteall")) {
                            answer.setText("Are you sure?");
                            answer.setReplyMarkup(KeyboardFactory.generateDeleteMarkup());
                            chatStateMap.put(chatId, ProcessorState.DELETE_ALL_WAIT_ANSWER);
                        }
                        if (text.equals("/help")) {
                            scenarioHelpCommand(answer);
                        }
                        if (text.equals("/showdiagram")) {
                            answer.setText("Which product chart you want to see?");
                            chatStateMap.put(chatId, ProcessorState.SHOW_DIAGRAM_WAIT_PRODUCT_ID);
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
                            chatStateMap.put(chatId, ProcessorState.NONE);
                        }
                        sendMessage(answer);
                    }
                }
            }
        }
    }

    private void scenarioAddRecord(String text, Long chatId, SendMessage answer) {
        commonAnswerEntity = new AddRecordUsecase().execute(chatId, text);
        answer.setText(commonAnswerEntity.getMessageForUser());
        chatStateMap.put(chatId, ProcessorState.NONE);
    }


    private void scenarioStart(SendMessage answer) {
        commonAnswerEntity = new StartUsecase().execute();
        answer.enableHtml(true);
        answer.setText(commonAnswerEntity.getMessageForUser());
    }

    private void scenarioShowAllGoods(Long chatId, SendMessage answer) {
        commonAnswerEntity = new ShowAllGoodsUsecase().execute(chatId);
        answer.enableHtml(true);
        answer.setText(commonAnswerEntity.getMessageForUser());
    }


    private void scenarioHelpCommand(SendMessage answer) {
        commonAnswerEntity = new HelpUsecase().execute();
        answer.enableHtml(true);
        answer.setText(commonAnswerEntity.getMessageForUser());
    }

    private void scenarioCreateImage(String text, String chatId, SendMessage answer) {
        try {
            CreateImageAnswerEntity createImageAnswerEntity = new ShowDiagramUsecase().execute(text);
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

    private void isItFirstTime(Long chatId, SendMessage answer) {
        if (!chatStateMap.containsKey(chatId)) {
            chatStateMap.put(chatId, ProcessorState.NONE);
            ReplyKeyboardMarkup commandsButton = KeyboardFactory.generateKeyboard();
            answer.setReplyMarkup(commandsButton);
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
