package me.kamsa23.trueSight.checks.movement;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class WaterWalk implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.WaterWalk.max-flags", 3);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Block below = p.getLocation().subtract(0, 0.1, 0).getBlock();
        Material type = below.getType();

        boolean walkingOnWater = (type == Material.WATER || type == Material.KELP || type == Material.SEAGRASS)
                && p.getLocation().getY() % 1.0 == 0.0
                && !p.isSwimming()
                && !p.isFlying();

        if (walkingOnWater) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged WaterWalk (on " + type + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "WaterWalk");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "WaterWalk");
            }
        }
    }
}