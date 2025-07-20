package me.kamsa23.trueSight.checks.misc;

import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.entity.Player;

import java.util.UUID;

public class InventorySpoof implements Listener {
    private final int maxClicksPerTick = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.InventorySpoof.max-clicks", 5);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.InventorySpoof.max-flags", 3);

    @EventHandler
    public void onClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        UUID id = p.getUniqueId();

        int clicks = FlagManager.getFlags(id); // misuse of flags map for click count
        clicks++;
        FlagManager.flagPlayer(id); // reuse flags as click count
        LogManager.log(p.getName() +
                " InventorySpoof click=" + clicks);

        if (clicks > maxClicksPerTick) {
            int flags = clicks; // real flags
            LogManager.log(p.getName() +
                    " flagged InventorySpoof (clicks=" + clicks + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "InventorySpoof");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "InventorySpoof");
            }
            FlagManager.resetFlags(id); // reset click count
        }
    }
}