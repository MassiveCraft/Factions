package com.massivecraft.factions.util;

import com.massivecraft.factions.*;
import com.massivecraft.factions.util.material.FactionMaterial;
import com.massivecraft.factions.util.particle.ParticleColor;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@SuppressWarnings("unchecked")
public class SeeChunkUtil extends BukkitRunnable {

    private Set<UUID> playersSeeingChunks = new HashSet<>();
    private boolean useColor;
    private Object effect;

    public SeeChunkUtil() {
        String effectName = P.p.getConfig().getString("see-chunk.particle", "REDSTONE");
        this.effect = P.p.particleProvider.effectFromString(effectName);
        this.useColor = P.p.getConfig().getBoolean("see-chunk.relational-useColor", true);

        P.p.getLogger().info(P.p.txt.parse("Using %s as the ParticleEffect for /f sc", P.p.particleProvider.effectName(effect)));
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

    public static void showPillars(Player me, FPlayer fme, Object effect, boolean useColor) {
        World world = me.getWorld();
        FLocation flocation = new FLocation(me);
        int chunkX = (int) flocation.getX();
        int chunkZ = (int) flocation.getZ();

        ParticleColor color = null;
        if (useColor) {
            ChatColor chatColor = Board.getInstance().getFactionAt(flocation).getRelationTo(fme).getColor();
            color = ParticleColor.fromChatColor(chatColor);
        }

        int blockX;
        int blockZ;

        blockX = chunkX * 16;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ, effect, color);

        blockX = chunkX * 16 + 16;
        blockZ = chunkZ * 16;
        showPillar(me, world, blockX, blockZ, effect, color);

        blockX = chunkX * 16;
        blockZ = chunkZ * 16 + 16;
        showPillar(me, world, blockX, blockZ, effect, color);

        blockX = chunkX * 16 + 16;
        blockZ = chunkZ * 16 + 16;
        showPillar(me, world, blockX, blockZ, effect, color);
    }

    public static void showPillar(Player player, World world, int blockX, int blockZ, Object effect, ParticleColor color) {
        // Lets start at the player's Y spot -30 to optimize
        for (int blockY = player.getLocation().getBlockY() - 30; blockY < player.getLocation().getBlockY() + 30; blockY++) {
            Location loc = new Location(world, blockX, blockY, blockZ);
            if (loc.getBlock().getType() != Material.AIR) {
                continue;
            }

            if (effect != null) {
                if (color == null) {
                    P.p.particleProvider.playerSpawn(player, effect, loc, 1);
                } else {
                    P.p.particleProvider.playerSpawn(player, effect, loc, color);
                }
            } else {
                Material mat = blockY % 5 == 0 ? FactionMaterial.from("REDSTONE_LAMP").get() : FactionMaterial.from("GLASS_PANE").get();
                VisualizeUtil.addLocation(player, loc, mat);
            }
        }
    }

}
