package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class InvalidYMotion implements Listener {
    private final double maxYSpeed = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.InvalidYMotion.max-speed", 1.0);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.InvalidYMotion.max-flags", 4);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double dy = e.getTo().getY() - e.getFrom().getY();
        if (Math.abs(dy) > maxYSpeed && !p.isFlying()) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged InvalidYMotion (dy=" + dy + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "InvalidYMotion");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "InvalidYMotion");
            }
        }
    }
}