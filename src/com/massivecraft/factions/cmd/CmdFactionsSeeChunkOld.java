package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.ps.PSFormatHumanSpace;

public class CmdFactionsSeeChunkOld extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSeeChunkOld()
	{
		// Aliases
		this.addAliases("sco");

		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		World world = me.getWorld();
		PS chunk = PS.valueOf(me.getLocation()).getChunk(true);
		int chunkX = chunk.getChunkX();
		int chunkZ = chunk.getChunkZ();
		
		// Apply
		int blockX;
		int blockZ;
		
		blockX = chunkX*16;
		blockZ = chunkZ*16;
		showPillar(me, world, blockX, blockZ);
		
		blockX = chunkX*16 + 15;
		blockZ = chunkZ*16;
		showPillar(me, world, blockX, blockZ);
		
		blockX = chunkX*16;
		blockZ = chunkZ*16 + 15;
		showPillar(me, world, blockX, blockZ);
		
		blockX = chunkX*16 + 15;
		blockZ = chunkZ*16 + 15;
		showPillar(me, world, blockX, blockZ);
		
		// Inform
		msg("<i>Visualized %s", chunk.toString(PSFormatHumanSpace.get()));
	}
	
	@SuppressWarnings("deprecation")
	public static void showPillar(Player player, World world, int blockX, int blockZ)
	{
		for (int blockY = 0; blockY < world.getMaxHeight(); blockY++)
		{
			Location loc = new Location(world, blockX, blockY, blockZ);
			if (loc.getBlock().getType() != Material.AIR) continue;
			int typeId = blockY % 5 == 0 ? Material.GLOWSTONE.getId() : Material.GLASS.getId();
			VisualizeUtil.addLocation(player, loc, typeId);
		}
	}
	
}
