package me.kamsa23.trueSight.managers;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Bukkit;

public class DeveloperManager {
    private static boolean debug = false;

    public static void init(JavaPlugin plugin) {
        // nothing to load
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void toggleDebug() {
        debug = !debug;
        String status = debug ? "enabled" : "disabled";
        Bukkit.getLogger().info("TrueSight debug mode " + status);
    }
}