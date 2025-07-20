package me.kamsa23.trueSight.checks.misc;

import me.kamsa23.trueSight.managers.FlagManager;
import me.kamsa23.trueSight.managers.LogManager;
import me.kamsa23.trueSight.managers.PunishmentManager;
import me.kamsa23.trueSight.managers.SetbackManager;
import me.kamsa23.trueSight.utils.ConfigUtil;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PotionEffectSpoof implements Listener {
    private final int maxFlags = ConfigUtil.getPlugin()
            .getConfig().getInt("checks.PotionEffectSpoof.max-flags", 3);

    @EventHandler
    public void onSplash(PotionSplashEvent event) {
        for (PotionEffect effect : event.getPotion().getEffects()) {
            PotionEffectType type = effect.getType();
            if (type == null || type.getKey() == null) {
                for (var entity : event.getAffectedEntities()) {
                    if (entity instanceof Player player) {
                        UUID id = player.getUniqueId();
                        int flags = FlagManager.getFlags(id) + 1;
                        FlagManager.flagPlayer(id);
                        LogManager.log(player.getName() +
                                " flagged PotionEffectSpoof (invalid effect), flags=" + flags);
                        if (flags >= maxFlags) {
                            PunishmentManager.punish(player, "PotionEffectSpoof");
                            FlagManager.resetFlags(id);
                        } else {
                            SetbackManager.applySetback(player, "PotionEffectSpoof");
                        }
                    }
                }
                break; // only flag once per splash
            }
        }
    }
}