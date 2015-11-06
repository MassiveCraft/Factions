package com.massivecraft.factions.cmd;

import java.util.Collections;
import java.util.Set;

import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.ps.PS;


public class CmdFactionsSetOne extends CmdFactionsSetXSimple
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetOne(boolean claim)
	{
		// Super
		super(claim);
		
		// Aliases
		this.addAliases("one");

		// Requirements
		this.addRequirements(RequirementIsPlayer.get());
		String node = claim ? Perm.CLAIM_ONE.node : Perm.UNCLAIM_ONE.node;
		this.addRequirements(RequirementHasPerm.get(node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public Set<PS> getChunks()
	{
		final PS chunk = PS.valueOf(me.getLocation()).getChunk(true);
		final Set<PS> chunks = Collections.singleton(chunk);
		return chunks;
	}
	
}
