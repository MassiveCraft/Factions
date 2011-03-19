package com.bukkit.mcteam.factions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class FLocation {

	private String worldName = "world";
	private long x = 0;
	private long z = 0;
	
	private final static transient double cellSize = 16;
	
	//----------------------------------------------//
	// Constructors
	//----------------------------------------------//
	
	public FLocation() {
		
	}
	
	public FLocation(String worldName, long x, long z) {
		this.worldName = worldName;
		this.x = x;
		this.z = z;
	}
	
	public FLocation(Location location) {
		this(location.getWorld().getName(), (long) Math.floor(location.getX() / cellSize) , (long) Math.floor(location.getZ() / cellSize));
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

	public void setX(long x) {
		this.x = x;
	}

	public long getZ() {
		return z;
	}

	public void setZ(long z) {
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
	
	// TODO hash code
	
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