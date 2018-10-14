package com.massivecraft.factions.util;

import com.darkblade12.particleeffect.ParticleEffect;
import com.massivecraft.factions.*;
import com.massivecraft.factions.util.material.FactionMaterial;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import static com.darkblade12.particleeffect.ParticleEffect.REDSTONE;
import static com.darkblade12.particleeffect.ParticleEffect.fromName;

public class SeeChunkUtil extends BukkitRunnable {

    private Set<UUID> playersSeeingChunks = new HashSet<>();
    private boolean useColor;
    private ParticleEffect effect;

    public SeeChunkUtil() {
        String effectName = P.p.getConfig().getString("see-chunk.particle", "REDSTONE");
        this.effect = fromName(effectName.toUpperCase());
        if (this.effect == null) {
            this.effect = REDSTONE;
        }
        this.useColor = this.effect.hasProperty(ParticleEffect.ParticleProperty.COLORABLE) && P.p.getConfig().getBoolean("see-chunk.relational-useColor", true);

        P.p.log(Level.INFO, "Using %s as the ParticleEffect for /f sc", effect.getName());
    }

    @Override
    public void run() {
        for (UUID playerId : playersSeeingChunks) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null) {
                playersSeeingChunks.remove(playerId);
                continue;
            }
            FPlayer fme = FPlayers.getInstance().getByPlayer(player);
            showPillars(player, fme, this.effect, useColor);
        }
    }

    public void updatePlayerInfo(UUID uuid, boolean toggle) {
        if (toggle) {
            playersSeeingChunks.add(uuid);
        } else {
            playersSeeingChunks.remove(uuid);
        }
    }

    public static void showPillars(Player me, FPlayer fme, ParticleEffect particleEffect, boolean useColor) {
        World world = me.getWorld();
        FLocation flocation = new FLocation(me);
        int chunkX = (int) flocation.getX();
        int chunkZ = (int) flocation.getZ();

        ParticleEffect.ParticleColor color = null;
        if (useColor) {
            ChatColor chatColor = Board.getInstance().getFactionAt(flocation).getRelationTo(fme).getColor();
            color = RelationalParticleColor.fromChatColor(chatColor);
        }

        int blockX;
        int blockZ;

        blockX = chunkX * 16;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ, particleEffect, color);

        blockX = chunkX * 16 + 16;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ, particleEffect, color);

        blockX = chunkX * 16;
        blockZ = chunkZ * 16 + 16;
        showPillar(me, world, blockX, blockZ, particleEffect, color);

        blockX = chunkX * 16 + 16;
        blockZ = chunkZ * 16 + 16;
        showPillar(me, world, blockX, blockZ, particleEffect, color);
    }

    public static void showPillar(Player player, World world, int blockX, int blockZ, ParticleEffect effect, ParticleEffect.ParticleColor color) {
        // Lets start at the player's Y spot -30 to optimize
        for (int blockY = player.getLocation().getBlockY() - 30; blockY < player.getLocation().getBlockY() + 30; blockY++) {
            Location loc = new Location(world, blockX, blockY, blockZ);
            if (loc.getBlock().getType() != Material.AIR) {
                continue;
            }

            if (effect != null) {
                if (color == null) {
                    effect.display(0, 0, 0, 10, 1, loc, player);
                } else {
                    effect.display(color, loc, player);
                }
            } else {
                Material mat = blockY % 5 == 0 ? FactionMaterial.constant("REDSTONE_LAMP").get() : FactionMaterial.constant("GLASS_PANE").get();
                VisualizeUtil.addLocation(player, loc, mat);
            }
        }
    }

    public enum RelationalParticleColor {
        BLACK(new ParticleEffect.OrdinaryColor(0, 0, 0)),
        DARK_BLUE(new ParticleEffect.OrdinaryColor(0, 0, 190)),
        DARK_GREEN(new ParticleEffect.OrdinaryColor(0, 190, 0)),
        DARK_AQUA(new ParticleEffect.OrdinaryColor(0, 190, 190)),
        DARK_RED(new ParticleEffect.OrdinaryColor(190, 0, 0)),
        DARK_PURPLE(new ParticleEffect.OrdinaryColor(190, 0, 190)),
        GOLD(new ParticleEffect.OrdinaryColor(217, 163, 52)),
        GRAY(new ParticleEffect.OrdinaryColor(190, 190, 190)),
        DARK_GRAY(new ParticleEffect.OrdinaryColor(63, 63, 63)),
        BLUE(new ParticleEffect.OrdinaryColor(63, 63, 254)),
        GREEN(new ParticleEffect.OrdinaryColor(63, 254, 63)),
        AQUA(new ParticleEffect.OrdinaryColor(63, 254, 254)),
        RED(new ParticleEffect.OrdinaryColor(254, 63, 63)),
        LIGHT_PURPLE(new ParticleEffect.OrdinaryColor(254, 63, 254)),
        YELLOW(new ParticleEffect.OrdinaryColor(254, 254, 63)),
        WHITE(new ParticleEffect.OrdinaryColor(255, 255, 255));

        private ParticleEffect.OrdinaryColor ordinaryColor;

        RelationalParticleColor(ParticleEffect.OrdinaryColor ordinaryColor) {
            this.ordinaryColor = ordinaryColor;
        }

        public ParticleEffect.OrdinaryColor getOrdinaryColor() {
            return ordinaryColor;
        }

        public static ParticleEffect.OrdinaryColor fromChatColor(ChatColor chatColor) {
            for (RelationalParticleColor relational : values()) {
                if (relational.name().equals(chatColor.name())) {
                    return relational.getOrdinaryColor();
                }
            }
            return null;
        }
    }
}
