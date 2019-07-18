package com.massivecraft.factions.integration;

import com.massivecraft.factions.FLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IWorldguard {

    boolean isPVP(Player player);

    boolean playerCanBuild(Player player, Location loc);

    boolean checkForRegionsInChunk(FLocation flocation);

    String getVersion();

}
