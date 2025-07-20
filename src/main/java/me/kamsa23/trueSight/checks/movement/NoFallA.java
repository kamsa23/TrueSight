package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class NoFallA implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.NoFallA.max-flags", 3);

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.FALL && e.getDamage() == 0) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged NoFallA (no damage, flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "NoFallA");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "NoFallA");
            }
        }
    }
}