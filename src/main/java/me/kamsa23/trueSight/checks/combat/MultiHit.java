package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.*;

public class MultiHit implements Listener {
    private final int maxHitsPerTick = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.MultiHit.max-hits-per-tick", 1);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.MultiHit.max-flags", 3);

    private final Map<UUID, Integer> hitsThisTick = new HashMap<>();
    private long lastTick = getCurrentTick();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        long tick = getCurrentTick();
        UUID id = p.getUniqueId();

        if (tick != lastTick) {
            hitsThisTick.clear();
            lastTick = tick;
        }

        int count = hitsThisTick.getOrDefault(id, 0) + 1;
        hitsThisTick.put(id, count);

        if (count > maxHitsPerTick) {
            flagAndPunish(p, "MultiHit", count + " hits/tick");
            hitsThisTick.put(id, 0);
        }
    }

    private long getCurrentTick() {
        return System.currentTimeMillis() / 50;
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