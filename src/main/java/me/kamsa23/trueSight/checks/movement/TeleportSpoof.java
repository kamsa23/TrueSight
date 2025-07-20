package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class TeleportSpoof implements Listener {
    private final double maxDistance = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.TeleportSpoof.max-distance", 100.0);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.TeleportSpoof.max-flags", 3);

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Player p = e.getPlayer();
        double dist = e.getFrom().distance(e.getTo());
        if (dist > maxDistance && !e.getCause().name().contains("PLUGIN")) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged TeleportSpoof (distance=" + dist + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "TeleportSpoof");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "TeleportSpoof");
            }
        }
    }
}