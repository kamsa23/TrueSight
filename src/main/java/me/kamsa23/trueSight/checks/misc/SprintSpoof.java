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

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SprintSpoof {
    private final int maxToggles = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.SprintSpoof.max-toggles", 10);
    private final long windowMs = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.SprintSpoof.window-ms", 1000L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.SprintSpoof.max-flags", 3);

    private final ConcurrentMap<UUID, Counter> map = new ConcurrentHashMap<>();

    public SprintSpoof() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.ENTITY_ACTION) {
            @Override
            public void onPacketReceiving(PacketEvent ev) {
                int action = ev.getPacket().getIntegers().read(1);
                // 3=start sprint, 4=stop sprint
                if (action<3||action>4) return;
                UUID id = ev.getPlayer().getUniqueId();
                long now = System.currentTimeMillis();
                Counter c = map.get(id);
                if (c==null||now-c.start>windowMs) {
                    c=new Counter(now,1);
                    map.put(id,c);
                } else c.count++;
                if (c.count>maxToggles) {
                    int flags = FlagManager.getFlags(id)+1;
                    FlagManager.flagPlayer(id);
                    LogManager.log(ev.getPlayer().getName() +
                            " flagged SprintSpoof (toggles="+c.count+", flags="+flags+")");
                    if (flags>=maxFlags) {
                        PunishmentManager.punish(ev.getPlayer(),"SprintSpoof");
                        FlagManager.resetFlags(id);
                    } else {
                        SetbackManager.applySetback(ev.getPlayer(),"SprintSpoof");
                    }
                    c.count=0;
                }
            }
        });
    }

    private static class Counter { final long start; int count; Counter(long s,int c){start=s;count=c;} }
}