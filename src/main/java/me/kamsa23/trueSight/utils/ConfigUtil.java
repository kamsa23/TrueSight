package me.kamsa23.trueSight.utils;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class ConfigUtil {
    private static JavaPlugin plugin;
    private static YamlConfiguration messagesCfg;
    private static YamlConfiguration punishmentsCfg;
    private static YamlConfiguration alertsCfg;
    private static YamlConfiguration webhookCfg;

    public static void init(JavaPlugin pl) {
        plugin = pl;
        plugin.saveDefaultConfig();
        messagesCfg = loadYaml("messages.yml");
        punishmentsCfg = loadYaml("punishments.yml");
        alertsCfg = loadYaml("alerts.yml");
        webhookCfg = loadYaml("webhook.yml");
    }

    private static YamlConfiguration loadYaml(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) plugin.saveResource(fileName, false);
        return YamlConfiguration.loadConfiguration(file);
    }

    public static JavaPlugin getPlugin() { return plugin; }
    public static YamlConfiguration getMessages() { return messagesCfg; }
    public static YamlConfiguration getPunishments() { return punishmentsCfg; }
    public static YamlConfiguration getAlerts() { return alertsCfg; }
    public static YamlConfiguration getWebhookConfig() { return webhookCfg; }
}