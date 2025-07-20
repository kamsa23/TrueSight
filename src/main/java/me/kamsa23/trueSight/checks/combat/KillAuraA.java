package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.*;

public class KillAuraA implements Listener {
    private final int maxHitsPerSecond = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.KillAuraA.max-hits-per-second", 8);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.KillAuraA.max-flags", 4);

    private final Map<UUID, Integer> hits = new HashMap<>();
    private final Map<UUID, Long> lastReset = new HashMap<>();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();

        // reset every second
        if (!lastReset.containsKey(id) || now - lastReset.get(id) > 1000) {
            hits.put(id, 1);
            lastReset.put(id, now);
        } else {
            hits.put(id, hits.get(id) + 1);
        }

        int count = hits.get(id);
        if (count > maxHitsPerSecond) {
            flagAndPunish(p, "KillAuraA", count + " hits/sec");
            hits.put(id, 0);
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