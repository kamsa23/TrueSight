package me.kamsa23.trueSight.managers;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import org.bukkit.plugin.java.JavaPlugin;
import me.kamsa23.trueSight.TrueSight;

public class DiscordManager {
    private static boolean enabled;
    private static String webhookUrl;
    private static String format;

    public static void init(JavaPlugin plugin) {
        var cfg = ConfigUtil.getWebhookConfig();
        enabled = cfg.getBoolean("webhook.enabled", false);
        webhookUrl = cfg.getString("webhook.url", null);
        format = cfg.getString("webhook.format", "**TrueSight Alert**\nPlayer: `%player%`\nCheck: `%check%`\nTime: `%time%`");
    }

    public static void sendAlert(String player, String check) {
        if (!enabled || webhookUrl == null) return;
        try {
            String content = format
                    .replace("%player%", player)
                    .replace("%check%", check)
                    .replace("%time%", java.time.LocalDateTime.now().toString());
            byte[] body = ("{\"content\":\"" + content + "\"}").getBytes(StandardCharsets.UTF_8);

            HttpURLConnection conn = (HttpURLConnection) new URL(webhookUrl).openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);
            conn.getOutputStream().write(body);
            conn.getInputStream().close();
        } catch (Exception ex) {
            TrueSight.getInstance().getLogger().warning("Failed to send Discord alert: " + ex.getMessage());
        }
    }
}