package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.ps.PS;

import java.util.LinkedHashSet;
import java.util.Set;


public class CmdFactionsSetSquare extends CmdFactionsSetXRadius
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetSquare(boolean claim)
	{
		// Super
		super(claim);
		
		// Aliases
		this.addAliases("square");

		// Format
		this.setFormatOne("<h>%s<i> %s <h>%d <i>chunk %s<i> using square.");
		this.setFormatMany("<h>%s<i> %s <h>%d <i>chunks near %s<i> using square.");
		
		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
		Perm perm = claim ? Perm.CLAIM_SQUARE : Perm.UNCLAIM_SQUARE;
		this.addRequirements(RequirementHasPerm.get(perm));		
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<PS> getChunks() throws MassiveException
	{
		// Common Startup
		final PS chunk = PS.valueOf(me.getLocation()).getChunk(true);
		final Set<PS> chunks = new MassiveSet<>();
		
		chunks.add(chunk); // The center should come first for pretty messages
		
		Integer radiusZero = this.getRadiusZero();

		for (int dx = -radiusZero; dx <= radiusZero; dx++)
		{
			for (int dz = -radiusZero; dz <= radiusZero; dz++)
			{
				int x = chunk.getChunkX() + dx;
				int z = chunk.getChunkZ() + dz;
				
				chunks.add(chunk.withChunkX(x).withChunkZ(z));
			}
		}
		
		return chunks;
	}
	
}
