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

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            SendMessage answer = scenarioDeleteAllRecordsForUser(update);
            sendMessage(answer);
        } else {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                Long chatId = update.getMessage().getChatId();
                SendMessage answer = new SendMessage();
                isItFirstTime(chatId, answer);
                if (message.hasText()) {
                    String text = message.getText();
                    if (!chatStateMap.get(chatId).equals(ProcessorState.NONE)) {

                        if (chatStateMap.get(chatId).equals(ProcessorState.ADD_WAIT_LINK)) {
                            answer = scenarioAddRecord(text, chatId);
                            sendMessage(answer);
                        }

                        if (chatStateMap.get(chatId).equals(ProcessorState.SHOW_DIAGRAM_WAIT_PRODUCT_ID) &&
                                isNumeric(text)) {
                            SendPhoto photo = scenarioCreateImage(Long.valueOf(text), chatId);
                            sendPhoto(photo);
                        }
                    } else {
                        if (text.equals("/start")) {
                            answer = scenarioStart(chatId);
                            logger.info("User " + update.getMessage().getChatId() + " starts work");
                        }
                        if (text.equals("/add")) {
                            answer = scenarioAddWaitLink(chatId);
                        }
                        if (text.equals("/showall")) {
                            answer = scenarioShowAllGoods(chatId);
                        }
                        if (text.equals("/deleteall")) {
                            answer = scenarioDeleteAllWaitAnswer(chatId);
                        }
                        if (text.equals("/help")) {
                            answer = scenarioHelpCommand(chatId);
                        }
                        if (text.equals("/showdiagram")) {
                            answer = scenarioShowDiagrammWaitProducctId(chatId);
                        }
                        if (!text.equals("/start")
                                && !text.equals("/add")
                                && !text.equals("/showall")
                                && !text.equals("/deleteall")
                                && !text.equals("/help")
                                && !text.equals("/showdiagram")
                        ) {
                            answer = scenarioIncorrectInformation(chatId);
                            logger.info("User " + update.getMessage().getChat().getId() + " write incorrect command or information");
                        }
                        sendMessage(answer);
                    }
                }
            }
        }
    }

    private SendMessage scenarioIncorrectInformation(Long chatId) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText("It is not a correct command or expected information");
        chatStateMap.put(chatId, ProcessorState.NONE);
        return answer;
    }

    private SendMessage scenarioShowDiagrammWaitProducctId(Long chatId) {
        SendMessage answer = new SendMessage();
        answer.setChatId(chatId);
        answer.setText("Which product chart you want to see?");
        chatStateMap.put(chatId, ProcessorState.SHOW_DIAGRAM_WAIT_PRODUCT_ID);
        return answer;
    }

    private SendMessage scenarioDeleteAllWaitAnswer(Long chatId) {
        SendMessage answer = new SendMessage();
        answer.setText("Are you sure?");
        answer.setChatId(chatId);
        answer.setReplyMarkup(KeyboardFactory.generateDeleteMarkup());
        chatStateMap.put(chatId, ProcessorState.DELETE_ALL_WAIT_ANSWER);
        return answer;
    }

    private SendMessage scenarioAddWaitLink(Long chatId) {
        SendMessage answer = new SendMessage();
        answer.setText("Please enter the link");
        answer.setChatId(chatId);
        chatStateMap.put(chatId, ProcessorState.ADD_WAIT_LINK);
        return answer;
    }

    private SendMessage scenarioAddRecord(String text, Long chatId) {
        AddRecordAnswerEntity dto = new AddRecordUsecase().execute(chatId, text);
        SendMessage answer = new SendMessage();
        answer.setText(dto.getMessageForUser());
        answer.setChatId(chatId);
        chatStateMap.put(chatId, ProcessorState.NONE);
        return answer;
    }


    private SendMessage scenarioStart(Long chatId) {
        CommonAnswerEntity commonAnswerEntity = new StartUsecase().execute();
        SendMessage answer = new SendMessage();
        answer.enableHtml(true);
        answer.setText(commonAnswerEntity.getMessageForUser());
        answer.setChatId(chatId);
        return answer;
    }

    private SendMessage scenarioShowAllGoods(Long chatId) {
        ShowAllAnswerEntity answerEntity = new ShowAllGoodsUsecase().execute(chatId);
        SendMessage answer = new SendMessage();
        answer.enableHtml(true);
        answer.setChatId(chatId);
        answer.setText(answerEntity.getMessageForUser());
        return answer;
    }


    private SendMessage scenarioHelpCommand(Long chatId) {
        CommonAnswerEntity commonAnswerEntity = new HelpUsecase().execute();
        SendMessage answer = new SendMessage();
        answer.enableHtml(true);
        answer.setChatId(chatId);
        answer.setText(commonAnswerEntity.getMessageForUser());
        return answer;
    }

    private SendPhoto scenarioCreateImage(Long goodsId, Long chatId) {
        CreateImageAnswerEntity createImageAnswerEntity = new ShowDiagramUsecase().execute(goodsId);
        SendPhoto sendPhoto = new SendPhoto();
        if (createImageAnswerEntity.getAnswerEnum().equals(AnswerEnum.SUCCESSFUL)) {
            sendPhoto.setPhoto(createImageAnswerEntity.getImage());
            sendPhoto.setChatId(chatId);
        }
        chatStateMap.put(chatId, ProcessorState.NONE);
        return sendPhoto;
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

    private void sendPhoto(SendPhoto sendPhoto) {
        try {
            execute(sendPhoto);
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


    private SendMessage scenarioDeleteAllRecordsForUser(Update update) {
        Long chatId = Long.valueOf(update.getCallbackQuery().getFrom().getId());
        SendMessage answer = new SendMessage();
        isItFirstTime(chatId, answer);
        if (update.getCallbackQuery().getData().equals("yes") &&
                chatStateMap.get(chatId).equals(ProcessorState.DELETE_ALL_WAIT_ANSWER)) {
            CommonAnswerEntity commonAnswerEntity = new DeleteAllUsecase().execute(chatId);
            answer.setText(commonAnswerEntity.getMessageForUser());
        } else {
            answer.setText("The delete command has been canceled");
        }
        chatStateMap.put(chatId, ProcessorState.NONE);
        answer.setChatId(chatId);
        return answer;
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
