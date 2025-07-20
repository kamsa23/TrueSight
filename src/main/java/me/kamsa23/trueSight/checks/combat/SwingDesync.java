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
import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SwingDesync implements Listener {
    private final int maxSwings = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.SwingDesync.max-swings", 5);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.SwingDesync.max-flags", 3);

    private final ConcurrentMap<UUID, Integer> swingCount = new ConcurrentHashMap<>();

    public SwingDesync() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.ARM_ANIMATION) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                UUID id = event.getPlayer().getUniqueId();
                swingCount.merge(id, 1, Integer::sum);
            }
        });
    }

    @EventHandler
    public void onHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        UUID id = p.getUniqueId();
        int swings = swingCount.getOrDefault(id, 0);

        if (swings < 1) {
            flagAndPunish(p, "SwingDesync", "swings=" + swings);
        }

        swingCount.put(id, 0);
    }

    private void flagAndPunish(Player p, String check, String detail) {
        UUID id = p.getUniqueId();
        int flags = FlagManager.getFlags(id) + 1;
        FlagManager.flagPlayer(id);
        LogManager.log(p.getName() + " flagged " + check +
                " (" + detail + ", flags=" + flags + ")");
        if (flags >= maxFlags) {
            PunishmentManager.punish(p, check);
            FlagManager.resetFlags(id);
        } else {
            SetbackManager.applySetback(p, check);
        }
    }
}