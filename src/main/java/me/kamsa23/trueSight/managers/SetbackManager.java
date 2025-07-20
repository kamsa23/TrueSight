package me.kamsa23.trueSight.managers;

import org.bukkit.entity.Player;
import org.bukkit.Location;

public class SetbackManager {
    public static void init(org.bukkit.plugin.java.JavaPlugin plugin) {
        // nothing to load
    }

    public static void applySetback(Player player, String check) {
        // teleport to spawn or last safe location
        Location spawn = player.getWorld().getSpawnLocation();
        player.teleport(spawn);
        LogManager.log(player.getName() + " setback applied for " + check);
    }
}