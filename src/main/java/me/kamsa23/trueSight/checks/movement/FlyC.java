package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class FlyC implements Listener {
    private final double maxHoverTime = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.FlyC.max-hover-seconds", 2.0);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.FlyC.max-flags", 5);

    private final java.util.Map<UUID, Long> hoverStart = new java.util.HashMap<>();

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        if (p.getVelocity().length() == 0 && !p.isOnGround()) {
            hoverStart.putIfAbsent(id, System.currentTimeMillis());
            long elapsed = System.currentTimeMillis() - hoverStart.get(id);
            if (elapsed > maxHoverTime * 1000) {
                int flags = FlagManager.getFlags(id) + 1;
                FlagManager.flagPlayer(id);
                LogManager.log(p.getName() + " flagged FlyC (hover=" + elapsed + "ms, flags=" + flags + ")");
                if (flags >= maxFlags) {
                    PunishmentManager.punish(p, "FlyC");
                    FlagManager.resetFlags(id);
                } else {
                    SetbackManager.applySetback(p, "FlyC");
                }
                hoverStart.remove(id);
            }
        } else {
            hoverStart.remove(id);
        }
    }
}