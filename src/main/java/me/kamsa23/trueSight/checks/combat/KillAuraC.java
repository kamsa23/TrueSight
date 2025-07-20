package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.*;

public class KillAuraC implements Listener {
    private final long minIntervalMs = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.KillAuraC.min-interval-ms", 100L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.KillAuraC.max-flags", 3);

    private final Map<UUID, Long> lastHit = new HashMap<>();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        long prev = lastHit.getOrDefault(id, 0L);
        lastHit.put(id, now);

        if (now - prev < minIntervalMs) {
            flagAndPunish(p, "KillAuraC", "interval=" + (now - prev) + "ms");
        }
    }

    private void flagAndPunish(Player p, String check, String detail) {
        UUID id = p.getUniqueId();
        int flags = FlagManager.getFlags(id) + 1;
        FlagManager.flagPlayer(id);
        LogManager.log(p.getName() + " flagged " + check + " (" + detail + ", flags=" + flags + ")");
        if (flags >= maxFlags) {
            PunishmentManager.punish(p, check);
            FlagManager.resetFlags(id);
        } else {
            SetbackManager.applySetback(p, check);
        }
    }
}