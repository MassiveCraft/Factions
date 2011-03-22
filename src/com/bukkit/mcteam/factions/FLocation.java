package com.bukkit.mcteam.factions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FLocation {

	private String worldName = "world";
	private int x = 0;
	private int z = 0;
	
	private final static transient double cellSize = 16;
	
	//----------------------------------------------//
	// Constructors
	//----------------------------------------------//
	
	public FLocation() {
		
	}
	
	public FLocation(String worldName, int x, int z) {
		this.worldName = worldName;
		this.x = x;
		this.z = z;
	}
	
	public FLocation(Location location) {
		this(location.getWorld().getName(), (int) Math.floor(location.getX() / cellSize) , (int) Math.floor(location.getZ() / cellSize));
	}
	
	public FLocation(Player player) {
		this(player.getLocation());
	}
	
	public FLocation(FPlayer fplayer) {
		this(fplayer.getPlayer());
	}
	
	public FLocation(Block block) {
		this(block.getLocation());
	}
	
	//----------------------------------------------//
	// Getters and Setters
	//----------------------------------------------//
	
	public String getWorldName() {
		return worldName;
	}

	public void setWorldName(String worldName) {
		this.worldName = worldName;
	}
	
	public long getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public long getZ() {
		return z;
	}

	public void setZ(int z) {
		this.z = z;
	}
	
	public String getCoordString() {
		return ""+x+","+z;
	}

	//----------------------------------------------//
	// Misc
	//----------------------------------------------//
	
	public FLocation getRelative(int dx, int dz) {
		return new FLocation(this.worldName, this.x + dx, this.z + dz);
	}
	
	//----------------------------------------------//
	// Comparison
	//----------------------------------------------//
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof FLocation))
			return false;

		FLocation o = (FLocation) obj;
		return this.x == o.x && this.z == o.z && this.worldName.equals(o.worldName);
	}
}