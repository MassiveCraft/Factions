package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.World;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.mixin.MixinTeleport;
import com.massivecraft.massivecore.mixin.TeleporterException;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.teleport.Destination;
import com.massivecraft.massivecore.teleport.DestinationSimple;

public class CmdFactionsUnstuck extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsUnstuck()
	{
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// If the player is in a chunk ...
		final PS center = PS.valueOf(me.getLocation().getChunk());
		
		// ... that isn't free ...
		if (isFree(msender, center))
		{
			msg("<b>You don't seem to be stuck.");
			return;
		}
		
		// ... get the nearest free top location ...
		Location location = getNearestFreeTopLocation(msender, center);
		if (location == null)
		{
			msg("<b>No nearby chunk with %s<b> or build rights found.", FactionColl.get().getNone().describeTo(msender));
			return;
		}
		
		// ... and schedule a teleport.
		Destination destination = new DestinationSimple(PS.valueOf(location));
		try
		{
			MixinTeleport.get().teleport(me, destination, MConf.get().unstuckSeconds);
		}
		catch (TeleporterException e)
		{
			msg("<b>%s", e.getMessage());
		}
	}
	
	// Get the first free top location in the spiral.
	public static Location getNearestFreeTopLocation(MPlayer mplayer, PS ps)
	{
		List<PS> chunks = getChunkSpiral(ps);
		for (PS chunk : chunks)
		{
			if (!isFree(mplayer, chunk)) continue;
			Location ret = getTopLocation(chunk);
			if (ret == null) continue;
			return ret;
		}
		return null;
	}
	
	// Return the ground level top location for this ps.
	public static Location getTopLocation(PS ps)
	{
		Location ret = null;
		try
		{
			World world = ps.asBukkitWorld();
			
			int blockX = ps.getBlockX(true);
			int blockZ = ps.getBlockZ(true);
			int blockY = world.getHighestBlockYAt(blockX, blockZ);
			
			// We must have something to stand on.
			if (blockY <= 1) return null;
			
			double locationX = blockX + 0.5D;
			double locationY = blockY + 0.5D;
			double locationZ = blockZ + 0.5D;
			
			ret = new Location(world, locationX, locationY, locationZ);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return ret;
	}
	
	// With a "free" chunk we mean wilderness or that the player has build rights.
	public static boolean isFree(MPlayer mplayer, PS ps)
	{
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction.isNone()) return true;
		return MPerm.getPermBuild().has(mplayer, ps, false);
	}
	
	// Not exactly a spiral. But it will do.
	public static List<PS> getChunkSpiral(PS center)
	{
		// Create Ret
		List<PS> ret = new ArrayList<PS>();
		
		// Fill Ret
		center = center.getChunk(true);
		final int centerX = center.getChunkX();
		final int centerZ = center.getChunkZ();
		
		for (int delta = 1; delta <= MConf.get().unstuckChunkRadius; delta++)
		{
			int minX = centerX - delta;
			int maxX = centerX + delta;
			int minZ = centerZ - delta;
			int maxZ = centerZ + delta;
			
			for (int x = minX; x <= maxX; x++)
			{
				ret.add(center.withChunkX(x).withChunkZ(minZ));
				ret.add(center.withChunkX(x).withChunkZ(maxZ));
			}
			
			for (int z = minZ + 1; z <= maxZ - 1; z++)
			{
				ret.add(center.withChunkX(minX).withChunkZ(z));
				ret.add(center.withChunkX(maxX).withChunkZ(z));
			}
		}
		
		// Return Ret
		return ret;
	}
	
}
