package com.massivecraft.factions.cmd;

import java.util.LinkedHashSet;
import java.util.Set;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.ps.PS;


public class CmdFactionsSetCircle extends CmdFactionsSetXRadius
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetCircle(boolean claim)
	{
		// Super
		super(claim);
		
		// Aliases
		this.addAliases("circle");

		// Format
		this.setFormatOne("<h>%s<i> %s <h>%d <i>chunk %s<i> using circle.");
		this.setFormatMany("<h>%s<i> %s <h>%d <i>chunks near %s<i> using circle.");
		
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
		String node = claim ? Perm.CLAIM_CIRCLE.node : Perm.UNCLAIM_CIRCLE.node;
		this.addRequirements(RequirementHasPerm.get(node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<PS> getChunks() throws MassiveException
	{
		// Common Startup
		final PS chunk = PS.valueOf(me.getLocation()).getChunk(true);
		final Set<PS> chunks = new LinkedHashSet<PS>();
		
		chunks.add(chunk); // The center should come first for pretty messages
		
		Integer radiusZero = this.getRadiusZero();
		if (radiusZero == null) return null;
		
		double radiusSquared = radiusZero * radiusZero;
		
		for (int dx = -radiusZero; dx <= radiusZero; dx++)
		{
			for (int dz = -radiusZero; dz <= radiusZero; dz++)
			{
				if (dx*dx + dz*dz > radiusSquared) continue;
				
				int x = chunk.getChunkX() + dx;
				int z = chunk.getChunkZ() + dz;
				
				chunks.add(chunk.withChunkX(x).withChunkZ(z));
			}
		}
		
		return chunks;
	}
	
}
