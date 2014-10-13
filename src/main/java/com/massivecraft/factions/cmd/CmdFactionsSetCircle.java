package com.massivecraft.factions.cmd;

import java.util.LinkedHashSet;
import java.util.Set;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;


public class CmdFactionsSetCircle extends CmdFactionsSetXRadius
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetCircle()
	{
		// Aliases
		this.addAliases("c", "circle");

		// Requirements
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasPerm.get(Perm.SET_CIRCLE.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<PS> getChunks()
	{
		// Common Startup
		final PS chunk = PS.valueOf(me).getChunk(true);
		final Set<PS> chunks = new LinkedHashSet<PS>();
		
		Integer radiusZero = this.getRadiusZero();
		if (radiusZero == null) return null;
		
		double radius = radiusZero + 1.5; // Tweak ze magic constant to get ze requested behavior.
		double radiusSquared = radius * radius;
		
		chunks.add(chunk); // The center should come first for pretty messages
		
		final int xmin = chunk.getChunkX() - radiusZero;
		final int xmax = chunk.getChunkX() + radiusZero;
		final int zmin = chunk.getChunkZ() - radiusZero;
		final int zmax = chunk.getChunkZ() + radiusZero;
		
		for (int x = xmin; x <= xmax; x++)
		{
			for (int z = zmin; z <= zmax; z++)
			{
				double dx = x - chunk.getChunkX();
				double dz = z - chunk.getChunkZ();
				if (dx*dx + dz*dz > radiusSquared) continue;
				chunks.add(chunk.withChunkX(x).withChunkZ(z));
			}
		}
		
		return chunks;
	}
	
}
