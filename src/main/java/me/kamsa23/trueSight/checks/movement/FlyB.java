package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class FlyB implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.FlyB.max-flags", 5);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Material below = p.getLocation().subtract(0, 1, 0).getBlock().getType();
        if (below == Material.AIR && !p.isFlying() && !p.getAllowFlight() && p.getVelocity().length() == 0) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged FlyB (hovering over air, flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "FlyB");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "FlyB");
            }
        }
    }
}