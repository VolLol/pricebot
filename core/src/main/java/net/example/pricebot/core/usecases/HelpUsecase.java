package net.example.pricebot.core.usecases;

import net.example.pricebot.core.answerEntityes.CommonAnswerEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class HelpUsecase {

    private static final Logger logger = LoggerFactory.getLogger(HelpUsecase.class);

    public CommonAnswerEntity execute() {
        logger.info("Start execute help usecase");
        CommonAnswerEntity answer = new CommonAnswerEntity();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("This bot understand following command:  \n\n");
        for (Map.Entry entry : getAllCommands().entrySet())
            stringBuilder.append("<b>" + entry.getKey() + "</b>" + " - " + entry.getValue() + "\n\n");
        answer.setMessageForUser(stringBuilder.toString());
        return answer;
    }


    private Map<String, String> getAllCommands() {
        Map<String, String> commands = new HashMap<>();
        commands.put("/add", "Add new good");
        commands.put("/showall", "Show all goods");
        commands.put("/deleteall", "Delete all goods");
        commands.put("/help", "Show help");
        commands.put("/showdiagram", "Show diagram");
        return commands;
    }
}
