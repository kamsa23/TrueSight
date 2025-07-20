package me.kamsa23.trueSight.checks.misc;

import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class IllegalItemUse implements Listener {
    private final List<String> blacklisted = ConfigUtil.getPlugin()
            .getConfig().getStringList("checks.IllegalItemUse.blacklist");
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.IllegalItemUse.max-flags", 3);

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        ItemStack hand = e.getItem();
        if (hand == null) return;
        String type = hand.getType().name();
        if (blacklisted.contains(type)) {
            Player p = e.getPlayer();
            UUID id = p.getUniqueId();
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() +
                    " flagged IllegalItemUse (" + type + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "IllegalItemUse");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "IllegalItemUse");
            }
            e.setCancelled(true);
        }
    }
}