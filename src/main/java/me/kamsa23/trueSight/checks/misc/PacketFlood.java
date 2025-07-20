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
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PacketFlood {
    private final int maxPackets = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.PacketFlood.max-packets", 100);
    private final long windowMs = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.PacketFlood.window-ms", 1000L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.PacketFlood.max-flags", 5);

    private final ConcurrentMap<UUID, Counter> map = new ConcurrentHashMap<>();

    public PacketFlood() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.FLYING,
                PacketType.Play.Client.POSITION,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent ev) {
                Player p = ev.getPlayer();
                UUID id = p.getUniqueId();
                long now = System.currentTimeMillis();
                Counter c = map.get(id);
                if (c==null || now-c.start>windowMs) {
                    c = new Counter(now,1);
                    map.put(id,c);
                } else c.count++;
                if (c.count>maxPackets) {
                    int flags = FlagManager.getFlags(id)+1;
                    FlagManager.flagPlayer(id);
                    LogManager.log(p.getName() +
                            " flagged PacketFlood (count="+c.count+", flags="+flags+")");
                    if (flags>=maxFlags) {
                        PunishmentManager.punish(p,"PacketFlood");
                        FlagManager.resetFlags(id);
                    } else {
                        SetbackManager.applySetback(p,"PacketFlood");
                    }
                    c.count=0;
                }
            }
        });
    }

    private static class Counter {
        final long start;
        int count;
        Counter(long s,int c){start=s;count=c;}
    }
}