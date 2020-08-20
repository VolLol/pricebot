package net.example.pricebot.core.processors;

import net.example.pricebot.core.PriceBotServerConfig;
import net.example.pricebot.core.dto.*;
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TelegramPriceBotProcessor extends TelegramLongPollingBot {
    private static final Logger logger = LoggerFactory.getLogger(TelegramPriceBotProcessor.class);
    private final Map<Long, ProcessorState> chatStateMap = new HashMap<>();
    private final AddGoodsToWatchlistUsecase addGoodsToWatchlistUseCase;
    private final DeleteAllGoodsForCustomerUsecase deleteAllGoodsForCustomerUsecase;
    private final ShowAllGoodsFromWatchlistUsecase showAllGoodsFromWatchlistUsecase;
    private final ShowChatPriceChangesByGoodIdUsecase showChatPriceChangesByGoodIdUsecase;
    private final ShowAllCommandsUsecase showAllCommandsUsecase;
    private final ShowWelcomeMessageUseCase showWelcomeMessageUseCase;

    public TelegramPriceBotProcessor(SqlSessionFactory sqlSessionFactory) {
        this.addGoodsToWatchlistUseCase = new AddGoodsToWatchlistUsecase(sqlSessionFactory);
        this.deleteAllGoodsForCustomerUsecase = new DeleteAllGoodsForCustomerUsecase(sqlSessionFactory);
        this.showAllGoodsFromWatchlistUsecase = new ShowAllGoodsFromWatchlistUsecase(sqlSessionFactory);
        this.showChatPriceChangesByGoodIdUsecase = new ShowChatPriceChangesByGoodIdUsecase(sqlSessionFactory);
        this.showAllCommandsUsecase = new ShowAllCommandsUsecase();
        this.showWelcomeMessageUseCase = new ShowWelcomeMessageUseCase();

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
                answer.setChatId(telegramId);
                answer.setText("");
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
                            answer = scenarioShowDiagramWaitProductId(telegramId);
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

    private SendMessage scenarioShowDiagramWaitProductId(Long telegramId) {
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
        AddGoodsToWatchlistDTO dto = this.addGoodsToWatchlistUseCase.execute(telegramId, goodsUrl);
        SendMessage answer = new SendMessage();
        answer.setText(dto.getMessageForUser());
        answer.setChatId(telegramId);
        chatStateMap.put(telegramId, ProcessorState.NONE);
        return answer;
    }


    private SendMessage scenarioStart(Long telegramId) {
        CommonDTO commonDTO = this.showWelcomeMessageUseCase.execute();
        SendMessage answer = new SendMessage();
        answer.enableHtml(true);
        answer.setText(commonDTO.getMessageForUser());
        answer.setChatId(telegramId);
        return answer;
    }

    private SendMessage scenarioShowAllGoods(Long telegramId) {
        ShowAllGoodsFromWatchlistDTO answerEntity = this.showAllGoodsFromWatchlistUsecase.execute(telegramId);
        SendMessage answer = new SendMessage();
        answer.setChatId(telegramId);
        answer.setText(answerEntity.getMessageForUser());
        answer.enableHtml(true);
        return answer;
    }


    private SendMessage scenarioHelpCommand(Long telegramId) {
        CommonDTO commonDTO = this.showAllCommandsUsecase.execute();
        SendMessage answer = new SendMessage();
        answer.enableHtml(true);
        answer.setChatId(telegramId);
        answer.setText(commonDTO.getMessageForUser());
        return answer;
    }

    private SendPhoto scenarioCreateImage(Long goodsId, Long telegramId) {
        ShowChatPriceChangesByGoodIdDTO createImageAnswerEntity = this.showChatPriceChangesByGoodIdUsecase.execute(goodsId);
        SendPhoto sendPhoto = new SendPhoto();
        if (createImageAnswerEntity.getDTOEnum().equals(DTOEnum.SUCCESSFUL)) {
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
            CommonDTO commonDTO = this.deleteAllGoodsForCustomerUsecase.execute(telegramId);
            answer.setText(commonDTO.getMessageForUser());
        } else {
            answer.setText("The delete command has been canceled");
        }
        chatStateMap.put(telegramId, ProcessorState.NONE);
        answer.setChatId(telegramId);
        return answer;
    }

    @Override
    public String getBotUsername() {
        return PriceBotServerConfig.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        return PriceBotServerConfig.TOKEN;
    }
}
