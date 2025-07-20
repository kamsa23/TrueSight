package me.kamsa23.trueSight.checks.misc;

import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class BlockPlaceDesync implements Listener {
    private final int maxPending = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.BlockPlaceDesync.max-pending", 5);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.BlockPlaceDesync.max-flags", 3);

    // track blocks placed vs confirmed
    private final ConcurrentMap<UUID, Integer> pending = new ConcurrentHashMap<>();

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        int count = pending.getOrDefault(id, 0) + 1;
        pending.put(id, count);

        if (count > maxPending) {
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() +
                    " flagged BlockPlaceDesync (pending=" + count + ", flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "BlockPlaceDesync");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "BlockPlaceDesync");
            }
            pending.put(id, 0);
        }
    }
}