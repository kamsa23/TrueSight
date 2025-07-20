package me.kamsa23.trueSight.checks.combat;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.entity.Player;
import me.kamsa23.trueSight.managers.*;
import me.kamsa23.trueSight.utils.ConfigUtil;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PingSpoof implements Listener {
    private final int maxPing = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.PingSpoof.max-ping-ms", 500);
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.PingSpoof.max-flags", 3);

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        checkPing(e.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        // cleanup if needed
    }

    private void checkPing(Player p) {
        int ping = p.spigot().getPing();
        if (ping > maxPing) {
            flagAndPunish(p, "PingSpoof", "ping=" + ping + "ms");
        }
    }

    private void flagAndPunish(Player p, String check, String detail) {
        UUID id = p.getUniqueId();
        int flags = FlagManager.getFlags(id) + 1;
        FlagManager.flagPlayer(id);
        LogManager.log(p.getName() +
                " flagged " + check + " (" + detail + ", flags=" + flags + ")");
        if (flags >= maxFlags) {
            PunishmentManager.punish(p, check);
            FlagManager.resetFlags(id);
        } else {
            SetbackManager.applySetback(p, check);
        }
    }
}