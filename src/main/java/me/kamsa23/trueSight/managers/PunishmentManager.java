package me.kamsa23.trueSight.managers;

import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import me.kamsa23.trueSight.utils.ConfigUtil;

public class PunishmentManager {
    private static FileConfiguration punishCfg;

    public static void init(JavaPlugin plugin) {
        // Load your punishments.yml into a FileConfiguration
        punishCfg = ConfigUtil.getPunishments();
    }

    public static void punish(Player player, String check) {
        // e.g. checks.KillAuraA.type
        String type = punishCfg.getString(check + ".type", "").toLowerCase();
        if (type.isEmpty()) return;

        ConsoleCommandSender console = Bukkit.getConsoleSender();
        switch (type) {
            case "ban":
                String banCmd = punishCfg.getString(check + ".command",
                        "ban %player% Cheating");
                Bukkit.dispatchCommand(console,
                        banCmd.replace("%player%", player.getName()));
                break;

            case "kick":
                String kickMsg = punishCfg.getString(check + ".message",
                        "You have been kicked for cheating.");
                player.kickPlayer(kickMsg);
                break;

            case "warn":
                String warnMsg = punishCfg.getString(check + ".message",
                        "Warning: Cheat detected!");
                player.sendMessage(
                        ConfigUtil.getMessages().getString("prefix") + warnMsg
                );
                break;

            case "command":
                String cmd = punishCfg.getString(check + ".command", "");
                if (!cmd.isEmpty()) {
                    Bukkit.dispatchCommand(console,
                            cmd.replace("%player%", player.getName()));
                }
                break;

            default:
                // unknown type
        }

        LogManager.log(player.getName() + " punished: " + check);
        DiscordManager.sendAlert(player.getName(), check);
    }
}