package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class ReachA implements Listener {
    private final double maxReach = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.ReachA.max-reach", 3.5);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.ReachA.max-flags", 4);

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        double dx = p.getLocation().getX() - e.getEntity().getLocation().getX();
        double dz = p.getLocation().getZ() - e.getEntity().getLocation().getZ();
        double dist = Math.hypot(dx, dz);

        if (dist > maxReach) {
            flagAndPunish(p, "ReachA", "reach=" + String.format("%.2f", dist));
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