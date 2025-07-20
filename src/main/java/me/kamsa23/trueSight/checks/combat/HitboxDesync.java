package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class HitboxDesync implements Listener {
    private final double maxDesync = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.HitboxDesync.max-desync", 0.3);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.HitboxDesync.max-flags", 3);

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        double dist = p.getLocation().distance(e.getEntity().getLocation());
        double expected = e.getEntity().getWidth() / 2.0;
        if ((dist - expected) > maxDesync) {
            flagAndPunish(p, "HitboxDesync",
                    "extra=" + String.format("%.2f", dist - expected));
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