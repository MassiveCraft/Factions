package com.massivecraft.factions.integration;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.griefcraft.util.config.ConfigurationNode;
import com.massivecraft.factions.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

public class LWCFeatures {
    private static LWC lwc;

    public static void setup() {
        Plugin test = Bukkit.getServer().getPluginManager().getPlugin("LWC");
        if (!(test instanceof LWCPlugin) || !test.isEnabled()) return;

        lwc = ((LWCPlugin) test).getLWC();
        P.p.log("Successfully hooked into LWC!" + (P.p.getConfig().getBoolean("lwc.integration", false) ? "" : " Integration is currently disabled (\"lwc.integration\")."));
    }

    public static boolean getEnabled() {
        return P.p.getConfig().getBoolean("lwc.integration", false) && lwc != null;
    }

    public static void clearOtherChests(FLocation flocation, Faction faction) {
        Location location = new Location(Bukkit.getWorld(flocation.getWorldName()), flocation.getX() * 16, 5, flocation.getZ() * 16);
        if (location.getWorld() == null) return;  // world not loaded or something? cancel out to prevent error
        Chunk chunk = location.getChunk();
        BlockState[] blocks = chunk.getTileEntities();
        List<Block> chests = new LinkedList<Block>();

        for (int x = 0; x < blocks.length; x++) {
            if (isProtectedBlock(blocks[x].getBlock())) {
                chests.add(blocks[x].getBlock());
            }
        }

        for (int x = 0; x < chests.size(); x++) {
            if (lwc.findProtection(chests.get(x)) != null) {
                if (!faction.getFPlayers().contains(FPlayers.getInstance().getByPlayer(Bukkit.getServer().getPlayer(lwc.findProtection(chests.get(x)).getOwner()))))
                    lwc.findProtection(chests.get(x)).remove();
            }
        }
    }

    public static void clearAllChests(FLocation flocation) {
        Location location = new Location(Bukkit.getWorld(flocation.getWorldName()), flocation.getX() * 16, 5, flocation.getZ() * 16);
        if (location.getWorld() == null) return;  // world not loaded or something? cancel out to prevent error
        Chunk chunk = location.getChunk();
        BlockState[] blocks = chunk.getTileEntities();
        List<Block> chests = new LinkedList<Block>();

        for (int x = 0; x < blocks.length; x++) {
            if (isProtectedBlock(blocks[x].getBlock())) {
                chests.add(blocks[x].getBlock());
            }
        }

        for (int x = 0; x < chests.size(); x++) {
            if (lwc.findProtection(chests.get(x)) != null) {
                lwc.findProtection(chests.get(x)).remove();
            }
        }
    }

    private static boolean isProtectedBlock(Block block) {
        boolean bProtected = false;
        String blockName = block.getType().name().toLowerCase();
        ConfigurationNode bConf = lwc.getConfiguration().getNode("protections.blocks." + blockName);
        if (bConf != null) bProtected = true;

        return bProtected;
    }
}
