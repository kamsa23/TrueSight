package me.kamsa23.trueSight.managers;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import org.bukkit.plugin.java.JavaPlugin;

public class LogManager {
    private static File logFile;

    public static void init(JavaPlugin plugin) {
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) dataFolder.mkdirs();

        logFile = new File(dataFolder, "checks.log");
        try {
            if (!logFile.exists()) {
                logFile.createNewFile();
            }
        } catch (IOException ex) {
            plugin.getLogger().warning("Could not create checks.log: " + ex.getMessage());
        }
    }

    public static void log(String message) {
        try {
            String entry = "[" + java.time.LocalDateTime.now() + "] " + message + "\n";
            Files.write(logFile.toPath(), entry.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}