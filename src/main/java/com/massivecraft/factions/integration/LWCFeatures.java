package com.massivecraft.factions.integration;

import com.griefcraft.lwc.LWC;
import com.griefcraft.lwc.LWCPlugin;
import com.massivecraft.factions.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.plugin.Plugin;

import java.util.LinkedList;
import java.util.List;

public class LWCFeatures {
    private static LWC lwc;

    public static void setup() {
        Plugin test = Bukkit.getServer().getPluginManager().getPlugin("LWC");
        if (test == null || !test.isEnabled()) return;

        lwc = ((LWCPlugin) test).getLWC();
        P.p.log("Successfully hooked into LWC!" + (Conf.lwcIntegration ? "" : " Integration is currently disabled, though (\"lwcIntegration\")."));
    }

    public static boolean getEnabled() {
        return Conf.lwcIntegration && lwc != null;
    }

    public static void clearOtherChests(FLocation flocation, Faction faction) {
        Location location = new Location(Bukkit.getWorld(flocation.getWorldName()), flocation.getX() * 16, 5, flocation.getZ() * 16);
        if (location.getWorld() == null) return;  // world not loaded or something? cancel out to prevent error
        Chunk chunk = location.getChunk();
        BlockState[] blocks = chunk.getTileEntities();
        List<Block> chests = new LinkedList<Block>();

        for (int x = 0; x < blocks.length; x++) {
            if (blocks[x].getType() == Material.CHEST) {
                chests.add(blocks[x].getBlock());
            }
        }

        for (int x = 0; x < chests.size(); x++) {
            if (lwc.findProtection(chests.get(x)) != null) {
                if (!faction.getFPlayers().contains(FPlayers.i.get(lwc.findProtection(chests.get(x)).getOwner())))
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
            if (blocks[x].getType() == Material.CHEST) {
                chests.add(blocks[x].getBlock());
            }
        }

        for (int x = 0; x < chests.size(); x++) {
            if (lwc.findProtection(chests.get(x)) != null) {
                lwc.findProtection(chests.get(x)).remove();
            }
        }
    }
}
