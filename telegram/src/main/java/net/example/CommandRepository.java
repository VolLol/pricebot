package net.example;

import java.util.HashMap;
import java.util.Map;

public class CommandRepository {



    public static Map<String, String> getAllCommands() {
        Map<String, String> commands = new HashMap<>();
        commands.put("/add", "Add new good");
        commands.put("/showall", "Show all goods");
        commands.put("/deleteall", "Delete all goods");
        commands.put("/help", "Show help");
        commands.put("/showdiagram", "Show diagram");
        return commands;
    }


}
