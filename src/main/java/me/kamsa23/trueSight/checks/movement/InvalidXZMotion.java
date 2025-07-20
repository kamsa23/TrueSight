package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class InvalidXZMotion implements Listener {
    private final double maxXZSpeed = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.InvalidXZMotion.max-speed", 0.6);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.InvalidXZMotion.max-flags", 4);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double dx = e.getTo().getX() - e.getFrom().getX();
        double dz = e.getTo().getZ() - e.getFrom().getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > maxXZSpeed) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged InvalidXZMotion (speed=" + dist + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "InvalidXZMotion");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "InvalidXZMotion");
            }
        }
    }
}