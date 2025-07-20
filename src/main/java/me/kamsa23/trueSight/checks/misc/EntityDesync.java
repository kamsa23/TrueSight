package me.kamsa23.trueSight.checks.misc;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import me.kamsa23.trueSight.TrueSight;
import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.UUID;

public class EntityDesync {

    private final double maxDist = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.EntityDesync.max-distance", 6.0);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.EntityDesync.max-flags", 3);

    public EntityDesync() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                Player p = e.getPlayer();
                World world = p.getWorld();

                // read the target Entity via the packet's entity modifier
                Entity target = e.getPacket()
                        .getEntityModifier(world)
                        .read(0);
                if (target == null) return;

                double dist = p.getLocation().distance(target.getLocation());
                if (dist > maxDist) {
                    UUID uuid = p.getUniqueId();
                    int flags = FlagManager.getFlags(uuid) + 1;
                    FlagManager.flagPlayer(uuid);
                    LogManager.log(p.getName() +
                            " flagged EntityDesync (dist=" +
                            String.format("%.2f", dist) +
                            ", flags=" + flags + ")");
                    if (flags >= maxFlags) {
                        PunishmentManager.punish(p, "EntityDesync");
                        FlagManager.resetFlags(uuid);
                    } else {
                        SetbackManager.applySetback(p, "EntityDesync");
                    }
                }
            }
        });
    }
}