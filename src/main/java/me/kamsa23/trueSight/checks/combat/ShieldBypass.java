package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.Material;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class ShieldBypass implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.ShieldBypass.max-flags", 3);

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        ItemStack off = p.getInventory().getItemInOffHand();
        boolean holdingShield = off != null && off.getType() == Material.SHIELD;
        boolean blocking = p.isHandRaised();

        if (holdingShield && blocking && e.getDamage() > 0) {
            flagAndPunish(p, "ShieldBypass", "damage while blocking");
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