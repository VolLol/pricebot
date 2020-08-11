package net.example.pricebot.core.processors;

import net.example.pricebot.core.BotConfig;
import net.example.pricebot.core.answerEntityes.*;
import net.example.pricebot.core.usecases.*;
import net.example.pricebot.core.utils.KeyboardFactory;
import org.apache.ibatis.session.SqlSessionFactory;
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
    private final AddRecordUsecase addRecordUseCase;
    private final DeleteAllUsecase deleteAllUsecase;
    private final ShowAllGoodsUsecase showAllGoodsUsecase;
    private final ShowDiagramUsecase showDiagramUsecase;
    private final HelpUsecase helpUsecase;
    private final StartUsecase startUsecase;

    public TelegramPriceBotProcessor(SqlSessionFactory sqlSessionFactory) {
        this.addRecordUseCase = new AddRecordUsecase(sqlSessionFactory);
        this.deleteAllUsecase = new DeleteAllUsecase(sqlSessionFactory);
        this.showAllGoodsUsecase = new ShowAllGoodsUsecase(sqlSessionFactory);
        this.showDiagramUsecase = new ShowDiagramUsecase(sqlSessionFactory);
        this.helpUsecase = new HelpUsecase();
        this.startUsecase = new StartUsecase();

    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasCallbackQuery()) {
            SendMessage answer = scenarioDeleteAllRecordsForUser(update);
            sendMessage(answer);
        } else {
            if (update.hasMessage()) {
                Message message = update.getMessage();
                Long telegramId = update.getMessage().getChatId();
                SendMessage answer = new SendMessage();
                isItFirstTime(telegramId, answer);
                if (message.hasText()) {
                    String text = message.getText();
                    if (!chatStateMap.get(telegramId).equals(ProcessorState.NONE)) {

                        if (chatStateMap.get(telegramId).equals(ProcessorState.ADD_WAIT_LINK)) {
                            answer = scenarioAddRecord(text, telegramId);
                            sendMessage(answer);
                        }

                        if (chatStateMap.get(telegramId).equals(ProcessorState.SHOW_DIAGRAM_WAIT_PRODUCT_ID) &&
                                isNumeric(text)) {
                            SendPhoto photo = scenarioCreateImage(Long.valueOf(text), telegramId);
                            sendPhoto(photo);
                        }
                    } else {
                        if (text.equals("/start")) {
                            answer = scenarioStart(telegramId);
                            logger.info("User " + update.getMessage().getChatId() + " starts work");
                        }
                        if (text.equals("/add")) {
                            answer = scenarioAddWaitLink(telegramId);
                        }
                        if (text.equals("/showall")) {
                            answer = scenarioShowAllGoods(telegramId);
                        }
                        if (text.equals("/deleteall")) {
                            answer = scenarioDeleteAllWaitAnswer(telegramId);
                        }
                        if (text.equals("/help")) {
                            answer = scenarioHelpCommand(telegramId);
                        }
                        if (text.equals("/showdiagram")) {
                            answer = scenarioShowDiagrammWaitProducctId(telegramId);
                        }
                        if (!text.equals("/start")
                                && !text.equals("/add")
                                && !text.equals("/showall")
                                && !text.equals("/deleteall")
                                && !text.equals("/help")
                                && !text.equals("/showdiagram")
                        ) {
                            answer = scenarioIncorrectInformation(telegramId);
                            logger.info("User " + update.getMessage().getChat().getId() + " write incorrect command or information");
                        }
                        sendMessage(answer);
                    }
                }
            }
        }
    }

    private SendMessage scenarioIncorrectInformation(Long telegramId) {
        SendMessage answer = new SendMessage();
        answer.setChatId(telegramId);
        answer.setText("It is not a correct command or expected information");
        chatStateMap.put(telegramId, ProcessorState.NONE);
        return answer;
    }

    private SendMessage scenarioShowDiagrammWaitProducctId(Long telegramId) {
        SendMessage answer = new SendMessage();
        answer.setChatId(telegramId);
        answer.setText("Which product chart you want to see?");
        chatStateMap.put(telegramId, ProcessorState.SHOW_DIAGRAM_WAIT_PRODUCT_ID);
        return answer;
    }

    private SendMessage scenarioDeleteAllWaitAnswer(Long telegramId) {
        SendMessage answer = new SendMessage();
        answer.setText("Are you sure?");
        answer.setChatId(telegramId);
        answer.setReplyMarkup(KeyboardFactory.generateDeleteMarkup());
        chatStateMap.put(telegramId, ProcessorState.DELETE_ALL_WAIT_ANSWER);
        return answer;
    }

    private SendMessage scenarioAddWaitLink(Long telegramId) {
        SendMessage answer = new SendMessage();
        answer.setText("Please enter the link");
        answer.setChatId(telegramId);
        chatStateMap.put(telegramId, ProcessorState.ADD_WAIT_LINK);
        return answer;
    }

    private SendMessage scenarioAddRecord(String goodsUrl, Long telegramId) {
        AddRecordAnswerEntity dto = this.addRecordUseCase.execute(telegramId, goodsUrl);
        SendMessage answer = new SendMessage();
        answer.setText(dto.getMessageForUser());
        answer.setChatId(telegramId);
        chatStateMap.put(telegramId, ProcessorState.NONE);
        return answer;
    }


    private SendMessage scenarioStart(Long telegramId) {
        CommonAnswerEntity commonAnswerEntity = this.startUsecase.execute();
        SendMessage answer = new SendMessage();
        answer.enableHtml(true);
        answer.setText(commonAnswerEntity.getMessageForUser());
        answer.setChatId(telegramId);
        return answer;
    }

    private SendMessage scenarioShowAllGoods(Long telegramId) {
        ShowAllAnswerEntity answerEntity = this.showAllGoodsUsecase.execute(telegramId);
        SendMessage answer = new SendMessage();
        answer.enableHtml(true);
        answer.setChatId(telegramId);
        answer.setText(answerEntity.getMessageForUser());
        return answer;
    }


    private SendMessage scenarioHelpCommand(Long telegramId) {
        CommonAnswerEntity commonAnswerEntity = this.helpUsecase.execute();
        SendMessage answer = new SendMessage();
        answer.enableHtml(true);
        answer.setChatId(telegramId);
        answer.setText(commonAnswerEntity.getMessageForUser());
        return answer;
    }

    private SendPhoto scenarioCreateImage(Long goodsId, Long telegramId) {
        CreateImageAnswerEntity createImageAnswerEntity = this.showDiagramUsecase.execute(goodsId);
        SendPhoto sendPhoto = new SendPhoto();
        if (createImageAnswerEntity.getAnswerEnum().equals(AnswerEnum.SUCCESSFUL)) {
            sendPhoto.setPhoto(createImageAnswerEntity.getImage());
            sendPhoto.setChatId(telegramId);
        }
        chatStateMap.put(telegramId, ProcessorState.NONE);
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

    private void isItFirstTime(Long telegramId, SendMessage answer) {
        if (!chatStateMap.containsKey(telegramId)) {
            chatStateMap.put(telegramId, ProcessorState.NONE);
            ReplyKeyboardMarkup commandsButton = KeyboardFactory.generateKeyboard();
            answer.setReplyMarkup(commandsButton);
        }
    }


    private SendMessage scenarioDeleteAllRecordsForUser(Update update) {
        Long telegramId = Long.valueOf(update.getCallbackQuery().getFrom().getId());
        SendMessage answer = new SendMessage();
        isItFirstTime(telegramId, answer);
        if (update.getCallbackQuery().getData().equals("yes") &&
                chatStateMap.get(telegramId).equals(ProcessorState.DELETE_ALL_WAIT_ANSWER)) {
            CommonAnswerEntity commonAnswerEntity = this.deleteAllUsecase.execute(telegramId);
            answer.setText(commonAnswerEntity.getMessageForUser());
        } else {
            answer.setText("The delete command has been canceled");
        }
        chatStateMap.put(telegramId, ProcessorState.NONE);
        answer.setChatId(telegramId);
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
