package com.massivecraft.factions.cmd;

import java.util.LinkedHashSet;
import java.util.Set;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;


public class CmdFactionsClaim extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsClaim()
	{
		// Aliases
		this.addAliases("claim");

		// Args
		this.addOptionalArg("radius", "1");
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasPerm.get(Perm.CLAIM.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{	
		// Args
		Integer radius = this.arg(0, ARInteger.get(), 1);
		if (radius == null) return;
		
		final Faction newFaction = this.arg(1, ARFaction.get(), msenderFaction);
		if (newFaction == null) return;
		
		// MPerm
		if (newFaction.isNormal() && ! MPerm.getPermTerritory().has(msender, newFaction, true)) return;
		
		// Radius Claim Min
		if (radius < 1)
		{
			msg("<b>If you specify a radius, it must be at least 1.");
			return;
		}
		
		// Radius Claim Perm
		if (radius > 1 && ! Perm.CLAIM_RADIUS.has(sender, false))
		{
			msg("<b>You do not have permission to claim in a radius.");
			return;
		}
		
		// Radius Claim Max
		if (radius > MConf.get().radiusClaimRadiusLimit && ! msender.isUsingAdminMode())
		{
			msg("<b>The maximum radius allowed is <h>%s<b>.", MConf.get().radiusClaimRadiusLimit);
			return;
		}
		
		// Get Chunks
		final int radiusZero = radius -1;
		final PS chunk = PS.valueOf(me).getChunk(true);
		final int xmin = chunk.getChunkX() - radiusZero;
		final int xmax = chunk.getChunkX() + radiusZero;
		final int zmin = chunk.getChunkZ() - radiusZero;
		final int zmax = chunk.getChunkZ() + radiusZero;
		Set<PS> chunks = new LinkedHashSet<PS>();
		chunks.add(chunk); // The center should come first for pretty messages
		for (int x = xmin; x <= xmax; x++)
		{
			for (int z = zmin; z <= zmax; z++)
			{
				chunks.add(chunk.withChunkX(x).withChunkZ(z));
			}
		}
		
		// Apply / Inform
		msender.tryClaim(newFaction, chunks);
	}
	
}
