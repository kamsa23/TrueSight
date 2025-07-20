package me.kamsa23.trueSight.checks.combat;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.TrueSight;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PreAim implements Listener {
    private final long maxDelay = ConfigUtil.getPlugin()
            .getConfig().getLong("checks.PreAim.max-delay-ms", 100L);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.PreAim.max-flags", 3);

    private final Map<UUID, Long> lastLook = new ConcurrentHashMap<>();

    public PreAim() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent ev) {
                lastLook.put(ev.getPlayer().getUniqueId(), System.currentTimeMillis());
            }
        });
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        UUID id = p.getUniqueId();
        long now = System.currentTimeMillis();
        long last = lastLook.getOrDefault(id, 0L);
        if (now - last > maxDelay) {
            flagAndPunish(p, "PreAim", "delay=" + (now - last) + "ms");
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