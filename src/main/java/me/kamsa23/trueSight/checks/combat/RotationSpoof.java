package me.kamsa23.trueSight.checks.combat;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.PacketType;
import me.kamsa23.trueSight.TrueSight;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class RotationSpoof {
    private final double maxYawChange = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.RotationSpoof.max-yaw-change", 90.0);
    private final double maxPitchChange = ConfigUtil.getPlugin()
            .getConfig().getDouble("checks.RotationSpoof.max-pitch-change", 45.0);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.RotationSpoof.max-flags", 3);

    private final ConcurrentMap<UUID, Float> lastYaw = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Float> lastPitch = new ConcurrentHashMap<>();

    public RotationSpoof() {
        ProtocolManager pm = ProtocolLibrary.getProtocolManager();
        pm.addPacketListener(new PacketAdapter(TrueSight.getInstance(),
                PacketType.Play.Client.LOOK,
                PacketType.Play.Client.POSITION_LOOK) {
            @Override
            public void onPacketReceiving(PacketEvent e) {
                UUID id = e.getPlayer().getUniqueId();
                float yaw = e.getPacket().getFloat().read(0);
                float pitch = e.getPacket().getFloat().read(1);

                Float prevYaw = lastYaw.getOrDefault(id, yaw);
                Float prevPitch = lastPitch.getOrDefault(id, pitch);

                double dyaw = Math.abs(yaw - prevYaw);
                double dpitch = Math.abs(pitch - prevPitch);

                lastYaw.put(id, yaw);
                lastPitch.put(id, pitch);

                if (dyaw > maxYawChange || dpitch > maxPitchChange) {
                    flagAndPunish(e.getPlayer(), "RotationSpoof",
                            "Δyaw=" + String.format("%.1f", dyaw) + ", Δpitch=" + String.format("%.1f", dpitch));
                }
            }
        });
    }

    private void flagAndPunish(org.bukkit.entity.Player p, String check, String detail) {
        UUID id = p.getUniqueId();
        int flags = FlagManager.getFlags(id) + 1;
        FlagManager.flagPlayer(id);
        LogManager.log(p.getName() + " flagged " + check + " (" + detail + ", flags=" + flags + ")");
        if (flags >= maxFlags) {
            PunishmentManager.punish(p, check);
            FlagManager.resetFlags(id);
        } else {
            SetbackManager.applySetback(p, check);
        }
    }
}