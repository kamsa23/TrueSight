package me.kamsa23.trueSight.commands;

import org.bukkit.command.CommandSender;
import me.kamsa23.trueSight.managers.DeveloperManager;

public class DebugToggleCommand {
    public static void execute(CommandSender sender) {
        DeveloperManager.toggleDebug();
        boolean debug = DeveloperManager.isDebug();
        sender.sendMessage("Debug mode " + (debug ? "enabled" : "disabled"));
    }
}