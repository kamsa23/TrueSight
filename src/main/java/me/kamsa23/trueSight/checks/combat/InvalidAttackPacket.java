package me.kamsa23.trueSight.checks.combat;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import me.kamsa23.trueSight.TrueSight;
import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;

public class InvalidAttackPacket implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.InvalidAttackPacket.max-flags", 3);

    public InvalidAttackPacket() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent ev) {
                boolean invalid = false;
                try {
                    // attempt to read entity ID and action enum
                    int entityId = ev.getPacket().getIntegers().read(0);
                    ev.getPacket().getEnumEntityUseActions().read(0);
                    // basic sanity: entityId should be non-negative
                    if (entityId < 0) invalid = true;
                } catch (Exception ex) {
                    invalid = true;
                }

                if (invalid) {
                    Player p = ev.getPlayer();
                    UUID id = p.getUniqueId();
                    int flags = FlagManager.getFlags(id) + 1;
                    FlagManager.flagPlayer(id);
                    LogManager.log(p.getName() +
                            " flagged InvalidAttackPacket (malformed USE_ENTITY packet, flags=" + flags + ")");
                    if (flags >= maxFlags) {
                        PunishmentManager.punish(p, "InvalidAttackPacket");
                        FlagManager.resetFlags(id);
                    } else {
                        SetbackManager.applySetback(p, "InvalidAttackPacket");
                    }
                }
            }
        });
    }
}