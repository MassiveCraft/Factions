package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.massivecraft.factions.struct.Permission;

// !!!! This is just an experiment.
// Proof of concept. We could use fake block updates to visualize the territories.
public class CmdSeeChunk extends FCommand
{
	public CmdSeeChunk()
	{
		super();
		this.aliases.add("sc");
		this.aliases.add("seechunks");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.ADMIN.node;
		this.disableOnLock = false;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		Location meLoc = me.getLocation();
		// Which chunk are we standing in ATM?
		// This bit shifting is something like divide by 16 :P
		int chunkX = meLoc.getBlockX() >> 4;
		int chunkZ = meLoc.getBlockZ() >> 4;
		
		// Get the pillars for that chunk
		int blockX;
		int blockZ;
		
		blockX = chunkX*16;
		blockZ = chunkZ*16;
		showPillar(me, me.getWorld(), blockX, blockZ);
		
		blockX = chunkX*16 + 15;
		blockZ = chunkZ*16;
		showPillar(me, me.getWorld(), blockX, blockZ);
		
		blockX = chunkX*16;
		blockZ = chunkZ*16 + 15;
		showPillar(me, me.getWorld(), blockX, blockZ);
		
		blockX = chunkX*16 + 15;
		blockZ = chunkZ*16 + 15;
		showPillar(me, me.getWorld(), blockX, blockZ);
	}
	
	public void showPillar(Player player, World world, int blockX, int blockZ)
	{
		Location loc = new Location(world, blockX, 0, blockZ);
		//Block block = loc.getBlock();
		for (int blockY = 0; blockY <=127; blockY++)
		{
			loc.setY(blockY);
			if (loc.getBlock().getTypeId() != 0) continue;
			player.sendBlockChange(loc, blockY % 5 == 0 ? Material.GLOWSTONE : Material.GLASS, (byte) 0);
		}
	}
	
	// DEV DIRT BELOW...
	
	public ArrayList<Location> getChunkPillarLocations(int chunkX, int chunkZ)
	{
		ArrayList<Location> ret = new ArrayList<Location>();
		
		
		
		return ret;
	}
	
	public ArrayList<Location> getPillar(Block block)
	{
		ArrayList<Location> ret = new ArrayList<Location>();
		
		// y 0-127
		
		for (int i = 0; i <=127; i++)
		{
			
		}
		
		return ret;
	}
	
}
