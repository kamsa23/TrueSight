package me.kamsa23.trueSight.commands;

import org.bukkit.command.CommandSender;
import me.kamsa23.trueSight.utils.ConfigUtil;

public class ReloadCommand {
    public static void execute(CommandSender sender) {
        ConfigUtil.init(ConfigUtil.getPlugin());
        sender.sendMessage(ConfigUtil.getMessages().getString("commands.reload"));
    }
}