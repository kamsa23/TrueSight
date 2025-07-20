package me.kamsa23.trueSight.checks.movement;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import me.kamsa23.trueSight.TrueSight;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class TimerManip {
    private final long minInterval = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.TimerManip.min-interval-ms", 30);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.TimerManip.max-flags", 4);

    private final ConcurrentMap<UUID, Long> lastPacketTime = new ConcurrentHashMap<>();

    public TimerManip() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.FLYING,
                PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                Player p = e.getPlayer();
                UUID id = p.getUniqueId();
                long now = System.currentTimeMillis();
                long last = lastPacketTime.getOrDefault(id, now);
                long interval = now - last;
                lastPacketTime.put(id, now);

                if (interval < minInterval) {
                    int flags = FlagManager.getFlags(id) + 1;
                    FlagManager.flagPlayer(id);
                    LogManager.log(p.getName() + " flagged TimerManip (interval=" + interval + "ms, flags=" + flags + ")");
                    if (flags >= maxFlags) {
                        PunishmentManager.punish(p, "TimerManip");
                        FlagManager.resetFlags(id);
                    } else {
                        SetbackManager.applySetback(p, "TimerManip");
                    }
                }
            }
        });
    }
}