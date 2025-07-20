package me.kamsa23.trueSight.managers;

import me.kamsa23.trueSight.TrueSight;
import org.bukkit.plugin.PluginManager;

// misc
import me.kamsa23.trueSight.checks.misc.*;

// movement
import me.kamsa23.trueSight.checks.movement.*;

// combat
import me.kamsa23.trueSight.checks.combat.*;

public class CheckManager {
    public static void init(TrueSight plugin) {
        PluginManager pm = plugin.getServer().getPluginManager();

        // --------------------
        // MISC
        // --------------------
        pm.registerEvents(new FastBreak(), plugin);
        pm.registerEvents(new NoSlow(), plugin);
        pm.registerEvents(new WaterWalk(), plugin);
        pm.registerEvents(new PhaseA(), plugin);
        pm.registerEvents(new PhaseB(), plugin);
        pm.registerEvents(new BlockBreakDesync(), plugin);
        pm.registerEvents(new BlockPlaceDesync(), plugin);
        pm.registerEvents(new ElytraSpoof(), plugin);
        new EntityDesync();
        pm.registerEvents(new FakeTeleport(), plugin);
        pm.registerEvents(new GhostBlock(), plugin);
        pm.registerEvents(new IllegalItemUse(), plugin);
        pm.registerEvents(new InstantMine(), plugin);
        pm.registerEvents(new InventorySpoof(), plugin);
        pm.registerEvents(new PotionEffectSpoof(), plugin);
        new SneakSpoof();
        new SprintSpoof();
        pm.registerEvents(new VelocitySpoof(), plugin);

        // Packet‐only misc checks (no @EventHandler, so just instantiate):
        new PacketFlood();

        // --------------------
        // MOVEMENT
        // --------------------
        pm.registerEvents(new FlyA(), plugin);
        pm.registerEvents(new FlyB(), plugin);
        pm.registerEvents(new FlyC(), plugin);
        pm.registerEvents(new FlyD(), plugin);
        pm.registerEvents(new GroundSpoof(), plugin);
        pm.registerEvents(new InvalidXZMotion(), plugin);
        pm.registerEvents(new InvalidYMotion(), plugin);
        pm.registerEvents(new JumpBoostSpoof(), plugin);
        pm.registerEvents(new NoFallA(), plugin);
        pm.registerEvents(new NoFallB(), plugin);
        pm.registerEvents(new SpeedA(), plugin);
        pm.registerEvents(new SpeedB(), plugin);
        pm.registerEvents(new SpeedC(), plugin);
        pm.registerEvents(new StepCheck(), plugin);
        pm.registerEvents(new StrafeCheck(), plugin);
        pm.registerEvents(new TeleportSpoof(), plugin);

        // Packet‐only movement checks:
        new TimerManip();

        // --------------------
        // COMBAT
        // --------------------
        pm.registerEvents(new KillAuraA(), plugin);
        pm.registerEvents(new KillAuraB(), plugin);
        pm.registerEvents(new KillAuraC(), plugin);
        pm.registerEvents(new ReachA(), plugin);
        pm.registerEvents(new ReachB(), plugin);
        pm.registerEvents(new MultiHit(), plugin);
        pm.registerEvents(new CriticalsSpoof(), plugin);
        pm.registerEvents(new WallHit(), plugin);
        pm.registerEvents(new CrystalAura(), plugin);
        pm.registerEvents(new AnchorAura(), plugin);
        pm.registerEvents(new PingSpoof(), plugin);
        pm.registerEvents(new ShieldBypass(), plugin);
        pm.registerEvents(new SilentAura(), plugin);
        pm.registerEvents(new PreAim(), plugin);
        pm.registerEvents(new HitboxDesync(), plugin);
        pm.registerEvents(new SwingDesync(), plugin);

        // Packet‐only combat checks:
        new RotationSpoof();
        new InvalidAttackPacket();
    }
}