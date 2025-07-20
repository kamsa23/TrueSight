package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class SpeedA implements Listener {
    private final double maxSpeed = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.SpeedA.max-speed", 0.35);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.SpeedA.max-flags", 4);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double dx = e.getTo().getX() - e.getFrom().getX();
        double dz = e.getTo().getZ() - e.getFrom().getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);

        if (dist > maxSpeed && !p.isFlying()) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged SpeedA (speed=" + dist + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "SpeedA");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "SpeedA");
            }
        }
    }
}