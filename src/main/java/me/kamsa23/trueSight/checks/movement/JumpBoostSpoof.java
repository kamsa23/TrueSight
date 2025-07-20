package me.kamsa23.trueSight.checks.movement;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class JumpBoostSpoof implements Listener {
    private final double maxJumpHeight = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.JumpBoostSpoof.max-height", 1.5);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.JumpBoostSpoof.max-flags", 3);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        double dy = e.getTo().getY() - e.getFrom().getY();
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() + " flagged JumpBoostSpoof (dy=" + dy + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "JumpBoostSpoof");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "JumpBoostSpoof");
            }
        }
    }