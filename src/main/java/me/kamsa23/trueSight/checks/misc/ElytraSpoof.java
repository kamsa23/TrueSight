package me.kamsa23.trueSight.checks.misc;

import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ElytraSpoof implements Listener {
    private final double minFallDistance = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.ElytraSpoof.min-fall-distance", 3.0);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.ElytraSpoof.max-flags", 3);

    @EventHandler
    public void onGlide(EntityToggleGlideEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!e.isGliding()) return;

        double fall = p.getFallDistance();
        if (fall < minFallDistance) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() +
                    " flagged ElytraSpoof (fall=" + fall + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "ElytraSpoof");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "ElytraSpoof");
            }
        }
    }
}