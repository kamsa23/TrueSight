package me.kamsa23.trueSight.commands;

import org.bukkit.command.CommandSender;

public class TestCheckCommand {
    public static void execute(CommandSender sender, String[] args) {
        sender.sendMessage("Test flag triggered for " + (args.length > 1 ? args[1] : "unknown check"));
    }
}