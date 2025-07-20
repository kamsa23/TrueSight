package me.kamsa23.trueSight.checks.misc;

import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.Action;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GhostBlock implements Listener {
    private final int maxGhosts = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.GhostBlock.max-ghosts", 3);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.GhostBlock.max-flags", 5);

    private final Set<Block> placed = new HashSet<>();
    private final Set<Block> broken = new HashSet<>();

    @EventHandler
    public void onPlace(org.bukkit.event.block.BlockPlaceEvent e) {
        placed.add(e.getBlockPlaced());
    }

    @EventHandler
    public void onBreak(org.bukkit.event.block.BlockBreakEvent e) {
        Block b = e.getBlock();
        if (!placed.contains(b)) {
            broken.add(b);
            e.setCancelled(true);
            Player p = e.getPlayer();
            UUID id = p.getUniqueId();
            int ghosts = broken.size();
            if (ghosts > maxGhosts) {
                int flags = FlagManager.getFlags(id) + 1;
                FlagManager.flagPlayer(id);
                LogManager.log(p.getName() +
                        " flagged GhostBlock (ghosts=" + ghosts + ", flags=" + flags + ")");
                if (flags >= maxFlags) {
                    PunishmentManager.punish(p, "GhostBlock");
                    FlagManager.resetFlags(id);
                } else {
                    SetbackManager.applySetback(p, "GhostBlock");
                }
                broken.clear();
            }
        }
    }
}