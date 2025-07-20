package me.kamsa23.trueSight.punishments.types;

import me.kamsa23.trueSight.managers.ConfigUtil;
import org.bukkit.entity.Player;

public class KickPunishment {
    public void execute(Player player, String check) {
        String msg = ConfigUtil.getPunishments()
                .getString(check + ".message", "You have been kicked for cheating.");
        player.kickPlayer(msg);
    }
}