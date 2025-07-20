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

public class PhaseA implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.PhaseA.max-flags", 3);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Block block = p.getLocation().getBlock();
        Material type = block.getType();

        boolean insideSolid = type.isSolid() && type != Material.AIR;

        if (insideSolid && !p.isFlying()) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged PhaseA (inside " + type + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "PhaseA");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "PhaseA");
            }
        }
    }
}