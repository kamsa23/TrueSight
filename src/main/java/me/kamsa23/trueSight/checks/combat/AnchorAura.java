package me.kamsa23.trueSight.checks.combat;

import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.block.BlockFace;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AnchorAura implements Listener {
    private final long minInterval = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.AnchorAura.min-interval-ms", 200L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.AnchorAura.max-flags", 3);

    private final Map<UUID, Long> lastPlace = new ConcurrentHashMap<>();

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (e.getClickedBlock()==null) return;
        if (e.getItem()==null ||
                e.getItem().getType() != Material.RESPAWN_ANCHOR) return;

        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        long prev = lastPlace.getOrDefault(id, 0L);
        lastPlace.put(id, now);

        if (now - prev < minInterval) {
            flagAndPunish(p, "AnchorAura",
                    "interval=" + (now - prev) + "ms");
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