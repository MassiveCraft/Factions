package com.massivecraft.factions.cmd;

import java.util.Collections;
import java.util.Set;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;


public class CmdFactionsSetAuto extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetAuto()
	{
		// Aliases
		this.addAliases("a", "auto");

		// Args
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqHasPerm.get(Perm.SET_AUTO.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{	
		// Args
		final Faction newFaction = this.arg(0, ARFaction.get(), msenderFaction);
		
		// Disable?
		if (newFaction == null || newFaction == msender.getAutoClaimFaction())
		{
			msender.setAutoClaimFaction(null);
			msg("<i>Disabled auto-setting as you walk around.");
			return;
		}
		
		// MPerm Preemptive Check
		if (newFaction.isNormal() && ! MPerm.getPermTerritory().has(msender, newFaction, true)) return;
		
		// Apply / Inform
		msender.setAutoClaimFaction(newFaction);
		msg("<i>Now auto-setting <h>%s<i> land.", newFaction.describeTo(msender));
		
		// Chunks
		final PS chunk = PS.valueOf(me).getChunk(true);
		Set<PS> chunks = Collections.singleton(chunk);		
		
		// Apply / Inform
		msender.tryClaim(newFaction, chunks);
	}
	
}
