package net.example.pricebot.core.usecases;

import net.example.pricebot.core.dto.DTOEnum;
import net.example.pricebot.core.dto.CommonDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class ShowWelcomeMessageUseCase {
    private static final Logger logger = LoggerFactory.getLogger(ShowWelcomeMessageUseCase.class);

    public CommonDTO execute() {
        logger.info("Start execute start usecase");
        CommonDTO answer = new CommonDTO();
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Welcome to Avito ParserBot!\n\n" +
                "This bot understand following command:  \n\n");
        for (Map.Entry entry : getAllCommands().entrySet())
            stringBuilder.append("<b>" + entry.getKey() + "</b>" + " - " + entry.getValue() + "\n\n");
        answer.setDTOEnum(DTOEnum.SUCCESSFUL);
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
