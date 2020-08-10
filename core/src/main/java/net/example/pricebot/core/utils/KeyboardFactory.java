package net.example.pricebot.core.utils;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

public class KeyboardFactory {

    public static ReplyKeyboardMarkup generateKeyboard() {
        KeyboardRow firstRow = new KeyboardRow();
        firstRow.add("/add");
        firstRow.add("/showall");
        KeyboardRow secondRow = new KeyboardRow();
        secondRow.add("/deleteall");
        secondRow.add("/help");
        KeyboardRow thirdRow = new KeyboardRow();
        thirdRow.add("/showdiagram");
        ReplyKeyboardMarkup commandsButton = new ReplyKeyboardMarkup();
        commandsButton.setOneTimeKeyboard(true);
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(firstRow);
        rows.add(secondRow);
        rows.add(thirdRow);
        commandsButton.setKeyboard(rows);
        return commandsButton;
    }

    public static InlineKeyboardMarkup generateDeleteMarkup() {
        InlineKeyboardMarkup keyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> finishKeyboard = new ArrayList<>();
        List<InlineKeyboardButton> list = new ArrayList<>();
        InlineKeyboardButton yesButton = new InlineKeyboardButton();
        yesButton.setText("Yes");
        yesButton.setCallbackData("yes");
        list.add(yesButton);
        InlineKeyboardButton noButton = new InlineKeyboardButton();
        noButton.setText("No");
        noButton.setCallbackData("No");
        list.add(noButton);
        finishKeyboard.add(list);
        keyboardMarkup.setKeyboard(finishKeyboard);
        return keyboardMarkup;
    }
}
