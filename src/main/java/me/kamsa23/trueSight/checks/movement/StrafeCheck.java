package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class StrafeCheck implements Listener {
    private final double maxStrafeSpeed = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.StrafeCheck.max-speed", 0.3);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.StrafeCheck.max-flags", 3);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double dx = e.getTo().getX() - e.getFrom().getX();
        double dz = e.getTo().getZ() - e.getFrom().getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        float yaw = p.getLocation().getYaw();
        boolean strafing = Math.abs(yaw % 90) < 10; // rough strafe detection

        if (strafing && dist > maxStrafeSpeed && !p.isFlying()) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged StrafeCheck (speed=" + dist + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "StrafeCheck");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "StrafeCheck");
            }
        }
    }
}