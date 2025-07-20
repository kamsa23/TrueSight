package me.kamsa23.trueSight.punishments.types;

import me.kamsa23.trueSight.managers.ConfigUtil;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class BanPunishment {
    public void execute(Player player, String check) {
        String cmd = ConfigUtil.getPunishments()
                .getString(check + ".command", "ban %player% Cheating detected");
        ConsoleCommandSender console = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(console, cmd.replace("%player%", player.getName()));
    }
}