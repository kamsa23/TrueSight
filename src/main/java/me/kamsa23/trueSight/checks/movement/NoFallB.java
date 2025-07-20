package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class NoFallB implements Listener {
    private final double minFallDistance = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.NoFallB.min-fall-distance", 3.0);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.NoFallB.max-flags", 3);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (p.getFallDistance() > minFallDistance && p.isOnGround()) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged NoFallB (fall=" + p.getFallDistance() + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "NoFallB");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "NoFallB");
            }
        }
    }
}