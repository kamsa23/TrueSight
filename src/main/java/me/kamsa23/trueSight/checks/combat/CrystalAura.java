package me.kamsa23.trueSight.checks.combat;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class CrystalAura implements Listener {
    private final long minInterval = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.CrystalAura.min-interval-ms", 200L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.CrystalAura.max-flags", 3);

    private final Map<UUID, Long> lastBreak = new ConcurrentHashMap<>();

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        if (e.getBlock().getType() != Material.END_CRYSTAL) return;
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        long prev = lastBreak.getOrDefault(id, 0L);
        lastBreak.put(id, now);

        if (now - prev < minInterval) {
            flagAndPunish(p, "CrystalAura",
                    "interval=" + (now - prev) + "ms");
        }
    }

    private void flagAndPunish(Player p, String check, String detail) {
        UUID id = p.getUniqueId();
        int flags = FlagManager.getFlags(id) + 1;
        FlagManager.flagPlayer(id);
        LogManager.log(p.getName() +
                " flagged " + check + " (" + detail + ", flags=" + flags + ")");
        if (flags >= maxFlags) {
            PunishmentManager.punish(p, check);
            FlagManager.resetFlags(id);
        } else {
            SetbackManager.applySetback(p, check);
        }
    }
}