package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.ps.PSFormatHumanSpace;

public class CmdFactionsSeeChunk extends FCommand
{
	public CmdFactionsSeeChunk()
	{
		this.addAliases("sc", "seechunk");
		
		//this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.SEE_CHUNK.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform()
	{
		// Args
		World world = me.getWorld();
		PS chunk = PS.valueOf(me).getChunk(true);
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
	
	public static void showPillar(Player player, World world, int blockX, int blockZ)
	{
		for (int blockY = 0; blockY < world.getMaxHeight(); blockY++)
		{
			Location loc = new Location(world, blockX, blockY, blockZ);
			if (loc.getBlock().getTypeId() != 0) continue;
			int typeId = blockY % 5 == 0 ? Material.GLOWSTONE.getId() : Material.GLASS.getId();
			VisualizeUtil.addLocation(player, loc, typeId);
		}
	}
	
}
