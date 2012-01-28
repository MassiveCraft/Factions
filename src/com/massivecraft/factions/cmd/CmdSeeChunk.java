package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.util.VisualizeUtil;

public class CmdSeeChunk extends FCommand
{
	public CmdSeeChunk()
	{
		super();
		this.aliases.add("sc");
		this.aliases.add("seechunk");
		
		this.permission = Permission.SEE_CHUNK.node;
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
		for (int blockY = 0; blockY <=127; blockY++)
		{
			loc.setY(blockY);
			if (loc.getBlock().getTypeId() != 0) continue;
			VisualizeUtil.addLocation(player, loc.clone(), blockY % 5 == 0 ? Material.GLOWSTONE.getId() : Material.GLASS.getId());
		}
	}
	
}
