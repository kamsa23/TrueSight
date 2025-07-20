package me.kamsa23.trueSight.punishments.types;

import me.kamsa23.trueSight.managers.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class CommandPunishment {
    public void execute(Player player, String check) {
        String cmd = ConfigUtil.getPunishments()
                .getString(check + ".command", "");
        if (!cmd.isEmpty()) {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            Bukkit.dispatchCommand(console, cmd.replace("%player%", player.getName()));
        }
    }
}