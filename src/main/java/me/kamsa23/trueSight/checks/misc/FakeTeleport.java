package me.kamsa23.trueSight.checks.misc;

// ProtocolLib imports
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.PacketType;                     // ← correct PacketType
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;

// Bukkit imports
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;                            // ← implement Listener
import org.bukkit.event.EventHandler;                       // ← import EventHandler
import org.bukkit.event.player.PlayerTeleportEvent;

// Your plugin imports
import me.kamsa23.trueSight.TrueSight;
import me.kamsa23.trueSight.utils.ConfigUtil;
import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class FakeTeleport implements Listener {
    private final long maxAckTime = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.FakeTeleport.max-ack-ms", 1000L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.FakeTeleport.max-flags", 3);

    private final ConcurrentMap<Integer, Long> teleportTimes = new ConcurrentHashMap<>();

    public FakeTeleport() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.TELEPORT_ACCEPT) {       // ← this constant now resolves
            @Override
            public void onPacketReceiving(PacketEvent e) {
                int id = e.getPacket().getIntegers().read(0);
                long now = System.currentTimeMillis();
                Long sent = teleportTimes.remove(id);

                if (sent == null || now - sent > maxAckTime) {
                    Player p = e.getPlayer();
                    UUID uuid = p.getUniqueId();
                    int flags = FlagManager.getFlags(uuid) + 1;
                    FlagManager.flagPlayer(uuid);
                    LogManager.log(p.getName() +
                            " flagged FakeTeleport (ackTime=" +
                            (sent == null ? "null" : (now - sent) + "ms") +
                            ", flags=" + flags + ")");
                    if (flags >= maxFlags) {
                        PunishmentManager.punish(p, "FakeTeleport");
                        FlagManager.resetFlags(uuid);
                    } else {
                        SetbackManager.applySetback(p, "FakeTeleport");
                    }
                }
            }
        });
    }

    @EventHandler
    public void onServerTeleport(PlayerTeleportEvent e) {
        // record when the server teleports the player
        teleportTimes.put(e.getPlayer().getEntityId(),
                System.currentTimeMillis());
    }
}