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

public class PhaseB implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.PhaseB.max-flags", 3);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        Block fromBlock = e.getFrom().getBlock();
        Block toBlock = e.getTo().getBlock();

        boolean movedThroughSolid = fromBlock.getType().isSolid()
                && toBlock.getType().isSolid()
                && !fromBlock.equals(toBlock)
                && !p.isFlying();

        if (movedThroughSolid) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged PhaseB (from " + fromBlock.getType() + " to " + toBlock.getType() + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "PhaseB");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "PhaseB");
            }
        }
    }
}