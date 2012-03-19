package com.massivecraft.factions;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.massivecraft.factions.util.MiscUtil;

public class FLocation
{

	private String worldName = "world";
	private int x = 0;
	private int z = 0;
	
	// TODO: It would be wise to ask yourself is there is a better solution to creating loads of new object
	// This object is create many times.
	
	//----------------------------------------------//
	// Constructors
	//----------------------------------------------//
	
	public FLocation()
	{
		
	}
	
	public FLocation(String worldName, int x, int z)
	{
		this.worldName = worldName;
		this.x = x;
		this.z = z;
	}
	
	public FLocation(Location location)
	{
		this( location.getWorld().getName(), blockToChunk(location.getBlockX()), blockToChunk(location.getBlockZ()) );
	}
	
	public FLocation(Player player)
	{
		this(player.getLocation());
	}
	
	public FLocation(FPlayer fplayer)
	{
		this(fplayer.getPlayer());
	}
	
	public FLocation(Block block)
	{
		this(block.getLocation());
	}
	
	//----------------------------------------------//
	// Getters and Setters
	//----------------------------------------------//
	
	public String getWorldName()
	{
		return worldName;
	}
	
	public World getWorld()
	{
		return Bukkit.getWorld(worldName);
	}

	public void setWorldName(String worldName)
	{
		this.worldName = worldName;
	}
	
	public long getX()
	{
		return x;
	}

	public void setX(int x)
	{
		this.x = x;
	}

	public long getZ()
	{
		return z;
	}

	public void setZ(int z)
	{
		this.z = z;
	}
	
	public String getCoordString()
	{
		return ""+x+","+z;
	}
	
	@Override
	public String toString() {
		return "["+this.getWorldName()+","+this.getCoordString()+"]";
	}

	//----------------------------------------------//
	// Block/Chunk/Region Value Transformation
	//----------------------------------------------//

	// bit-shifting is used because it's much faster than standard division and multiplication
	public static int blockToChunk(int blockVal)
	{	// 1 chunk is 16x16 blocks
		return blockVal >> 4;   // ">> 4" == "/ 16"
	}

	public static int blockToRegion(int blockVal)
	{	// 1 region is 512x512 blocks
		return blockVal >> 9;   // ">> 9" == "/ 512"
	}

	public static int chunkToRegion(int chunkVal)
	{	// 1 region is 32x32 chunks
		return chunkVal >> 5;   // ">> 5" == "/ 32"
	}

	public static int chunkToBlock(int chunkVal)
	{
		return chunkVal << 4;   // "<< 4" == "* 16"
	}

	public static int regionToBlock(int regionVal)
	{
		return regionVal << 9;   // "<< 9" == "* 512"
	}

	public static int regionToChunk(int regionVal)
	{
		return regionVal << 5;   // "<< 5" == "* 32"
	}

	//----------------------------------------------//
	// Misc Geometry
	//----------------------------------------------//
	
	public FLocation getRelative(int dx, int dz)
	{
		return new FLocation(this.worldName, this.x + dx, this.z + dz);
	}
	
	public double getDistanceTo(FLocation that)
	{
		double dx = that.x - this.x;
		double dz = that.z - this.z;
		return Math.sqrt(dx*dx+dz*dz); 
	}
	
	//----------------------------------------------//
	// Some Geometry
	//----------------------------------------------//
	public Set<FLocation> getCircle(double radius)
	{
		Set<FLocation> ret = new LinkedHashSet<FLocation>();
		if (radius <= 0) return ret;
		
		int xfrom = (int) Math.floor(this.x - radius);
		int xto =   (int) Math.ceil(this.x + radius);
		int zfrom = (int) Math.floor(this.z - radius);
		int zto =   (int) Math.ceil(this.z + radius);
		
		for (int x=xfrom; x<=xto; x++)
		{
			for (int z=zfrom; z<=zto; z++)
			{
				FLocation potential = new FLocation(this.worldName, x, z);
				if (this.getDistanceTo(potential) <= radius)
				{
					ret.add(potential);
				}
			}	
		}
		
		return ret;
	}
	
	public static HashSet<FLocation> getArea(FLocation from, FLocation to)
	{
		HashSet<FLocation> ret = new HashSet<FLocation>();
		
		for (long x : MiscUtil.range(from.getX(), to.getX()))
		{
			for (long z : MiscUtil.range(from.getZ(), to.getZ()))
			{
				ret.add(new FLocation(from.getWorldName(), (int)x, (int)z));
			}
		}
		
		return ret;
	}
	
	//----------------------------------------------//
	// Comparison
	//----------------------------------------------//
	
	@Override
	public int hashCode()
	{
		// should be fast, with good range and few hash collisions: (x * 512) + z + worldName.hashCode
		return (this.x << 9) + this.z + (this.worldName != null ? this.worldName.hashCode() : 0);
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this)
			return true;
		if (!(obj instanceof FLocation))
			return false;

		FLocation that = (FLocation) obj;
		return this.x == that.x && this.z == that.z && ( this.worldName==null ? that.worldName==null : this.worldName.equals(that.worldName) );
	}
}