package com.massivecraft.factions;

import java.util.HashSet;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.massivecraft.factions.util.MiscUtil;

public class FLocation {

	private String worldName = "world";
	private int x = 0;
	private int z = 0;
	
//	private final static transient double cellSize = 16;
	
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
//		this(location.getWorld().getName(), (int) Math.floor(location.getX() / cellSize) , (int) Math.floor(location.getZ() / cellSize));
		// handy dandy rapid bitshifting instead of division
		this(location.getWorld().getName(), location.getBlockX() >> 4, location.getBlockZ() >> 4);
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
	
	@Override
	public String toString() {
		return "["+this.getWorldName()+","+this.getCoordString()+"]";
	}

	//----------------------------------------------//
	// Misc Geometry
	//----------------------------------------------//
	
	public FLocation getRelative(int dx, int dz) {
		return new FLocation(this.worldName, this.x + dx, this.z + dz);
	}
	
	public static HashSet<FLocation> getArea(FLocation from, FLocation to) {
		HashSet<FLocation> ret = new HashSet<FLocation>();
		
		for (long x : MiscUtil.range(from.getX(), to.getX())) {
			for (long z : MiscUtil.range(from.getZ(), to.getZ())) {
				ret.add(new FLocation(from.getWorldName(), (int)x, (int)z));
			}
		}
		
		return ret;
	}
	
	//----------------------------------------------//
	// Comparison
	//----------------------------------------------//
	
	@Override
	public int hashCode() {
		int hash = 3;
        hash = 19 * hash + (this.worldName != null ? this.worldName.hashCode() : 0);
        hash = 19 * hash + this.x;
        hash = 19 * hash + this.z;
        return hash;
	};
	
	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof FLocation))
			return false;

		FLocation that = (FLocation) obj;
		return this.x == that.x && this.z == that.z && ( this.worldName==null ? that.worldName==null : this.worldName.equals(that.worldName) );
	}
}