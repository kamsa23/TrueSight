package me.kamsa23.trueSight.checks.misc;

import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.entity.Player;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class NoSlow implements Listener {
    private final double maxSpeed = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.NoSlow.max-speed-xz", 0.11);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.NoSlow.max-flags", 5);

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item==null) return;
        String name = item.getType().name();
        boolean slowable = name.endsWith("_SWORD") || name.endsWith("_AXE")
                || name.equals("BOW") || name.endsWith("COOKED_BEEF");
        if (!slowable) return;

        double dx = e.getTo().getX() - e.getFrom().getX();
        double dz = e.getTo().getZ() - e.getFrom().getZ();
        double dist = Math.sqrt(dx*dx + dz*dz);
        if (dist > maxSpeed) {
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() +
                    " flagged NoSlow (speed=" + String.format("%.3f",dist) + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "NoSlow");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "NoSlow");
            }
        }
    }
}