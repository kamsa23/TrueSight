package me.kamsa23.trueSight.commands;

import org.bukkit.command.CommandSender;
import me.kamsa23.trueSight.utils.ConfigUtil;

public class HelpCommand {
    public static void execute(CommandSender sender) {
        for (String line : ConfigUtil.getMessages().getStringList("commands.help")) {
            sender.sendMessage(line.replace("%prefix%", ConfigUtil.getMessages().getString("prefix")));
        }
    }
}