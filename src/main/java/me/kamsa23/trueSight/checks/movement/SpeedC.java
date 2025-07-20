package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class SpeedC implements Listener {
    private final double maxSpeedOnIce = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.SpeedC.max-speed", 0.5);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.SpeedC.max-flags", 4);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Material block = p.getLocation().subtract(0, 1, 0).getBlock().getType();
        if (!(block.name().contains("ICE"))) return;

        double dx = e.getTo().getX() - e.getFrom().getX();
        double dz = e.getTo().getZ() - e.getFrom().getZ();
        double dist = Math.sqrt(dx * dx + dz * dz);
        if (dist > maxSpeedOnIce && !p.isFlying()) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged SpeedC (ice speed=" + dist + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "SpeedC");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "SpeedC");
            }
        }
    }
}