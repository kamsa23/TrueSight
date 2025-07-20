package me.kamsa23.trueSight.managers;

import org.bukkit.plugin.java.JavaPlugin;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;

public class PacketManager {
    private static ProtocolManager protocolManager;

    public static void init(JavaPlugin plugin) {
        protocolManager = ProtocolLibrary.getProtocolManager();
        // later: register packet listeners for flying, use_entity, etc.
    }

    public static ProtocolManager getProtocolManager() {
        return protocolManager;
    }
}