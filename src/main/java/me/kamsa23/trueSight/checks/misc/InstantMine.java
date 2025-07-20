package me.kamsa23.trueSight.checks.misc;

import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import org.bukkit.block.Block;
import org.bukkit.Material;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class InstantMine implements Listener {
    private final long minTime = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.InstantMine.min-time-ms", 300L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.InstantMine.max-flags", 4);

    private final ConcurrentMap<UUID, Long> lastBreak = new ConcurrentHashMap<>();

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        Block b = e.getBlock();
        // ignore very soft blocks
        if (b.getType()==Material.WATER || b.getType()==Material.LAVA) return;

        long now = System.currentTimeMillis();
        long prev = lastBreak.getOrDefault(id, now);
        lastBreak.put(id, now);

        if (now - prev < minTime) {
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() +
                    " flagged InstantMine (time=" + (now-prev) + "ms, flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "InstantMine");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "InstantMine");
            }
        }
    }
}