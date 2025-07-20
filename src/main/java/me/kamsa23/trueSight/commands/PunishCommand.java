package me.kamsa23.trueSight.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.PunishmentManager;

public class PunishCommand {
    public static void execute(CommandSender sender, String[] args) {
        if (args.length < 3) {
            sender.sendMessage("Usage: /truesight punish <player> <check>");
            return;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage("Player not found.");
            return;
        }

        String check = args[2];
        PunishmentManager.punish(target, check);
        sender.sendMessage("Punishment applied to " + target.getName() + " for " + check);
    }
}