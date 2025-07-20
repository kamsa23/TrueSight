package me.kamsa23.trueSight.managers;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.scheduler.BukkitRunnable;

public class FlagManager {
    private static final HashMap<UUID, Integer> flags = new HashMap<>();
    private static final int DECAY_SECONDS = 60;

    public static void init(org.bukkit.plugin.java.JavaPlugin plugin) {
        // optional: schedule decay task
        new BukkitRunnable() {
            @Override
            public void run() {
                flags.replaceAll((uuid, count) -> Math.max(0, count - 1));
            }
        }.runTaskTimer(plugin, 20L * DECAY_SECONDS, 20L * DECAY_SECONDS);
    }

    public static void flagPlayer(UUID id) {
        flags.put(id, flags.getOrDefault(id, 0) + 1);
    }

    public static int getFlags(UUID id) {
        return flags.getOrDefault(id, 0);
    }

    public static void resetFlags(UUID id) {
        flags.remove(id);
    }
}