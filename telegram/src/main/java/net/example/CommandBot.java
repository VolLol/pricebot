package net.example;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.File;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandBot extends TelegramLongPollingBot {
    private static String STATE = "NONE";
    private static Boolean executeState = false;


    @Override
    public void onUpdateReceived(Update update) {
        SendMessage answer = new SendMessage();
        answer.setChatId(update.getMessage().getChatId());
        if (update.hasMessage()) {
            Message message = update.getMessage();
            if (message.hasText()) {
                String text = message.getText();
                if (executeState) {
                    if (STATE.equals("ADD WAIT LINK")) {
                        Pattern linkPattern = Pattern.compile("^https\\:\\/\\/www\\.avito\\.ru\\/.+");
                        Matcher matcher = linkPattern.matcher(text);
                        if (matcher.matches()) {
                            answer.setText("This good add to the watchlist");
                            STATE = "NONE";
                            executeState = false;
                        } else {
                            answer.setText("Can't use this link. Please write another ");
                            STATE = "ADD WAIT LINK";
                        }
                    }

                    if (STATE.equals("DELETE ALL WAIT ANSWER")) {
                        if (text.equalsIgnoreCase("yes")) {
                            answer.setText("Watchlist has been cleared");

                        } else {
                            answer.setText("The delete command has been canceled");
                        }
                        executeState = false;
                    }

                    if (STATE.equals("SHOW DIAGRAM WAIT PRODUCT ID")) {
                        SendPhoto sendPhoto = new SendPhoto();
                        File file = new File("C:\\Develop\\pricebot\\telegram\\src\\main\\java\\net\\example\\TelegramBotPrices-architecture.jpg");
                        sendPhoto.setPhoto(file);
                        if (text.equals("3")) {
                            try {
                                sendPhoto.setChatId(update.getMessage().getChatId());
                                execute(sendPhoto);
                            } catch (TelegramApiException e) {
                                e.printStackTrace();
                            }
                            answer.setText("Show chart with good id = " + text);
                        } else {
                            answer.setText("Can not find good with id = " + text);
                        }
                        STATE = "NONE";
                        executeState = false;
                    }

                    try {
                        execute(answer);
                    } catch (
                            TelegramApiException e) {
                        e.printStackTrace();
                    }
                } else {
                    if (CommandRepository.getAllCommands().containsKey(text)) {

                        if (text.equals("/add")) {
                            answer.setText("Please enter the link");
                            STATE = "ADD WAIT LINK";
                            executeState = true;
                        }
                        if (text.equals("/showall")) {
                            answer.enableHtml(true);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("<b>Id Title  Price</b>\n");
                            for (int i = 1; i < 6; i++) {
                                stringBuilder.append("<b>" + i + "</b>  goodTitle" + i + " " + i * 1000 + "\n");
                            }
                            answer.setText(stringBuilder.toString());
                        }
                        if (text.equals("/deleteall")) {
                            answer.setText("Are you sure?[Yes/No]");
                            STATE = "DELETE ALL WAIT ANSWER";
                            executeState = true;
                        }
                        if (text.equals("/help")) {
                            answer.enableHtml(true);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("This bot understand following command:  \n");
                            for (Map.Entry entry : CommandRepository.getAllCommands().entrySet())
                                stringBuilder.append("<b>" + entry.getKey() + "</b>" + " - " + entry.getValue() + "\n\n");
                            answer.setText(stringBuilder.toString());
                        }
                        if (text.equals("/showdiagram")) {
                            answer.setText("Which product chart you want to see?");
                            STATE = "SHOW DIAGRAM WAIT PRODUCT ID";
                            executeState = true;
                        }

                    } else {
                        answer.setText("Its not a command");
                    }

                    try {
                        execute(answer);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
            }
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
