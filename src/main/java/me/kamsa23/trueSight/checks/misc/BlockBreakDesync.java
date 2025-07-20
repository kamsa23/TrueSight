package me.kamsa23.trueSight.checks.misc;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import me.kamsa23.trueSight.TrueSight;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class BlockBreakDesync implements Listener {
    private final long minBreakTime = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.BlockBreakDesync.min-break-ms", 500L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.BlockBreakDesync.max-flags", 3);

    // track when START_DESTROY_BLOCK packet arrives
    private final Map<UUID, Long> startTime = new ConcurrentHashMap<>();

    public BlockBreakDesync() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.BLOCK_DIG) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                int action = event.getPacket().getIntegers().read(0);
                if (action == 0) { // START_DESTROY_BLOCK
                    startTime.put(event.getPlayer().getUniqueId(),
                            System.currentTimeMillis());
                }
            }
        });
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player p = e.getPlayer();
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        long started = startTime.getOrDefault(id, now);
        long delta = now - started;

        if (delta < minBreakTime) {
            int flags = FlagManager.getFlags(id) + 1;
            FlagManager.flagPlayer(id);
            LogManager.log(p.getName() +
                    " flagged BlockBreakDesync (time=" + delta + "ms, flags=" + flags + ")");
            if (flags >= maxFlags) {
                PunishmentManager.punish(p, "BlockBreakDesync");
                FlagManager.resetFlags(id);
            } else {
                SetbackManager.applySetback(p, "BlockBreakDesync");
            }
        }
    }
}