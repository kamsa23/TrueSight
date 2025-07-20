package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class ReachB implements Listener {
    private final double maxVerticalReach = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.ReachB.max-vertical-reach", 1.5);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.ReachB.max-flags", 3);

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        double dy = Math.abs(p.getLocation().getY() - e.getEntity().getLocation().getY());

        if (dy > maxVerticalReach) {
            flagAndPunish(p, "ReachB", "dy=" + String.format("%.2f", dy));
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