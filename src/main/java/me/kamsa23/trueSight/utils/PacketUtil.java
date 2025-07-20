package me.kamsa23.trueSight.utils;

import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketContainer;

public class PacketUtil {
    public static boolean isFlyingPacket(PacketEvent event) {
        PacketContainer packet = event.getPacket();
        return packet.getType().name().contains("FLYING")
                || packet.getType().name().contains("POSITION");
    }

    public static boolean isMovementPacket(PacketEvent event) {
        String name = event.getPacket().getType().name();
        return name.contains("POSITION") || name.contains("LOOK");
    }

    public static boolean isAttackPacket(PacketEvent event) {
        return event.getPacket().getType().name().equals("USE_ENTITY");
    }
}