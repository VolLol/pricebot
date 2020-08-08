package net.example.pricebot.core.utils;

import java.util.HashMap;
import java.util.Map;

public class CommandsRepository {
    public static Map<String, String> getAllCommands() {
        Map<String, String> commands = new HashMap<>();
        commands.put("/start", "Start working with bot");
        commands.put("/add", "Add new good");
        commands.put("/showall", "Show all goods");
        commands.put("/deleteall", "Delete all goods");
        commands.put("/help", "Show help");
        commands.put("/showdiagram", "Show diagram");
        return commands;
    }

}
