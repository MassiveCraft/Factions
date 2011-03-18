package com.bukkit.mcteam.factions;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;


public class Coord {
	protected static transient int cellSize = 16;
	public int x, z;
	
	public Coord(int x, int z) {
		this.x = x;
		this.z = z;
	}
	
	// TODO implements cloneable
	public Coord(Coord coord) {
		this.x = coord.x;
		this.z = coord.z;
	}
	
	public Coord() {
		// Noarg constructor for google gson.
	}
	
	public Coord getRelative(int dx, int dz) {
		return new Coord(this.x + dx, this.z + dz);
	}

	public static Coord from(int x, int z) {
		return new Coord(x / cellSize - (x < 0 ? 1 : 0), z / cellSize - (z < 0 ? 1 : 0));
	}

	public static Coord from(Player player) {
		return from(player.getLocation());
	}
	
	public static Coord from(FPlayer follower) {
		return from(follower.getPlayer());
	}

	public static Coord from(Location loc) {
		return from(loc.getBlockX(), loc.getBlockZ());
	}

	public static Coord parseCoord(Block block) {
		return from(block.getX(), block.getZ());
	}

	@Override
	public String toString() {
		return this.x + "," + this.z;
	}
	
	@Override
	public int hashCode() {
		int result = 17;
		result = 31 * result + x;
		result = 31 * result + z;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (!(obj instanceof Coord))
			return false;

		Coord o = (Coord) obj;
		return this.x == o.x && this.z == o.z;
	}
}
