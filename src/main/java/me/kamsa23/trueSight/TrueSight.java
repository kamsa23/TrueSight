package me.kamsa23.trueSight;

import org.bukkit.plugin.java.JavaPlugin;
import me.kamsa23.trueSight.managers.ConfigUtil;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.DiscordManager;
import me.kamsa23.trueSight.managers.ExploitManager;
import me.kamsa23.trueSight.managers.CheckManager;
import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.managers.PacketManager;
import me.kamsa23.trueSight.managers.DeveloperManager;
import me.kamsa23.trueSight.commands.TrueSightCommand;

public class TrueSight extends JavaPlugin {
    private static TrueSight instance;

    @Override
    public void onEnable() {
        instance = this;

        // Save default config and resources
        saveDefaultConfig();
        saveResource("messages.yml", false);
        saveResource("punishments.yml", false);
        saveResource("alerts.yml", false);
        saveResource("webhook.yml", false);

        // Initialize managers
        ConfigUtil.init(this);
        LogManager.init(this);
        DiscordManager.init(this);
        ExploitManager.init(this);
        CheckManager.init(this);
        FlagManager.init(this);
        PunishmentManager.init(this);
        SetbackManager.init(this);
        PacketManager.init(this);
        DeveloperManager.init(this);

        // Register command
        if (getCommand("truesight") != null) {
            getCommand("truesight").setExecutor(new TrueSightCommand());
        } else {
            getLogger().warning("Command 'truesight' not found in plugin.yml!");
        }

        getLogger().info("TrueSight AntiCheat enabled. All systems online.");
    }

    @Override
    public void onDisable() {
        getLogger().info("TrueSight AntiCheat disabled. Shutting down all checks.");
    }

    public static TrueSight getInstance() {
        return instance;
    }
}