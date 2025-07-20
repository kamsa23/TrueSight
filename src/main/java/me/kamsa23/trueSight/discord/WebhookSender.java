package me.kamsa23.trueSight.discord;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WebhookSender {
    public static void send(String webhookUrl, String content) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setDoOutput(true);

            String json = "{\"content\":\"" + escape(content) + "\"}";
            byte[] payload = json.getBytes(StandardCharsets.UTF_8);

            OutputStream os = conn.getOutputStream();
            os.write(payload);
            os.flush();
            os.close();

            conn.getInputStream().close(); // trigger the request
        } catch (Exception e) {
            System.out.println("[TrueSight] Failed to send Discord webhook: " + e.getMessage());
        }
    }

    private static String escape(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n");
    }
}