package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class CriticalsSpoof implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.CriticalsSpoof.max-flags", 3);

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;

        // A true critical hit requires the player to be falling.
        boolean onGround = p.isOnGround();
        double fallDist = p.getFallDistance();
        if (onGround && fallDist < 0.1 && e.getDamage() > 1.0) {
            flagAndPunish(p, "CriticalsSpoof",
                    "fallDist=" + String.format("%.2f", fallDist));
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