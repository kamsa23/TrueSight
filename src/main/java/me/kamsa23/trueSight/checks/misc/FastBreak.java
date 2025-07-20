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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FastBreak implements Listener {
    private final long minInterval = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.FastBreak.min-interval-ms", 200L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.FastBreak.max-flags", 5);
    private final ConcurrentMap<UUID, Long> lastBreak = new ConcurrentHashMap<>();

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        long prev = lastBreak.getOrDefault(id, 0L);
        lastBreak.put(id, now);
        if (now - prev < minInterval) {
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() +
                    " flagged FastBreak (interval=" + (now-prev) + "ms, flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "FastBreak");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "FastBreak");
            }
        }
    }
}