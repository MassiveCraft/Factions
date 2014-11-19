package com.massivecraft.factions.cmd;

import java.util.LinkedHashSet;
import java.util.Set;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;


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
		this.addAliases("s", "square");

		// Format
		this.setFormatOne("<h>%s<i> %s <h>%d <i>chunk %s<i> using square.");
		this.setFormatMany("<h>%s<i> %s <h>%d <i>chunks near %s<i> using square.");
		
		// Requirements
		this.addRequirements(ReqIsPlayer.get());
		String node = claim ? Perm.CLAIM_SQUARE.node : Perm.UNCLAIM_SQUARE.node;
		this.addRequirements(ReqHasPerm.get(node));		
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Set<PS> getChunks()
	{
		// Common Startup
		final PS chunk = PS.valueOf(me.getLocation()).getChunk(true);
		final Set<PS> chunks = new LinkedHashSet<PS>();
		
		chunks.add(chunk); // The center should come first for pretty messages
		
		Integer radiusZero = this.getRadiusZero();
		if (radiusZero == null) return null;
		
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
