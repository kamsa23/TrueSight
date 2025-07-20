package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.Bukkit;

import java.util.*;

public class KillAuraB implements Listener {
    private final int maxHitsPerTick = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.KillAuraB.max-hits-per-tick", 1);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.KillAuraB.max-flags", 3);

    private final Map<UUID, Integer> tickHits = new HashMap<>();
    private long lastTick = getTick();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        long currentTick = getTick();
        UUID id = p.getUniqueId();

        if (currentTick != lastTick) {
            tickHits.clear();
            lastTick = currentTick;
        }

        int count = tickHits.getOrDefault(id, 0) + 1;
        tickHits.put(id, count);

        if (count > maxHitsPerTick) {
            flagAndPunish(p, "KillAuraB", count + " hits/tick");
        }
    }

    private long getTick() {
        return System.currentTimeMillis() / 50;
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