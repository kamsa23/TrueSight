package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class GroundSpoof implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.GroundSpoof.max-flags", 3);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        boolean spoofed = p.isOnGround() && p.getLocation().subtract(0, 0.1, 0).getBlock().getType().isAir();
        if (spoofed) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged GroundSpoof (flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "GroundSpoof");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "GroundSpoof");
            }
        }
    }
}