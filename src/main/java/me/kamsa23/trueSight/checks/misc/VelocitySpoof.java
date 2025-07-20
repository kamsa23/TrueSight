package me.kamsa23.trueSight.checks.misc;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class VelocitySpoof implements Listener {
    private final double minKB = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.VelocitySpoof.min-knockback", 0.2);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.VelocitySpoof.max-flags", 3);

    private final ConcurrentMap<UUID, Long> lastHit = new ConcurrentHashMap<>();

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player)) return;
        Player p = (Player) e.getDamager();
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        lastHit.put(id, now);
    }

    @EventHandler
    public void onVelocity(org.bukkit.event.player.PlayerVelocityEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        long hitTime = lastHit.getOrDefault(id, 0L);
        if (System.currentTimeMillis() - hitTime > 1000) return;

        double vy = e.getVelocity().getY();
        if (vy < minKB) {
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() +
                    " flagged VelocitySpoof (vy=" + String.format("%.2f", vy) + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "VelocitySpoof");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "VelocitySpoof");
            }
        }
    }
}