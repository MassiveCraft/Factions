package com.massivecraft.factions.integration;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IWorldguard {

    boolean isPVP(Player player);

    boolean playerCanBuild(Player player, Location loc);

    boolean checkForRegionsInChunk(Chunk chunk);

    String getVersion();

}
