package com.massivecraft.factions.integration;

import com.massivecraft.factions.FLocation;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IWorldguard {

    public boolean isPVP(Player player);

    public boolean playerCanBuild(Player player, Location loc);

    public boolean checkForRegionsInChunk(FLocation flocation);

}
