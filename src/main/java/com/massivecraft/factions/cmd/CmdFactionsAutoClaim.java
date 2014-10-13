package com.massivecraft.factions.cmd;

import java.util.Collections;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsAutoClaim extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsAutoClaim()
	{
		// Aliases
		this.addAliases("autoclaim");

		// Args
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.AUTOCLAIM.node));
		this.addRequirements(ReqIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{	
		// Args
		Faction forFaction = this.arg(0, ARFaction.get(), msenderFaction);
		
		if (forFaction == null || forFaction == msender.getAutoClaimFaction())
		{
			msender.setAutoClaimFaction(null);
			msg("<i>Auto-claiming of land disabled.");
			return;
		}
		
		// MPerm
		if (forFaction.isNormal() && !MPerm.getPermTerritory().has(msender, forFaction, true)) return;
		
		msender.setAutoClaimFaction(forFaction);
		
		msg("<i>Now auto-claiming land for <h>%s<i>.", forFaction.describeTo(msender));
		
		msender.tryClaim(forFaction, Collections.singletonList(PS.valueOf(me).getChunk(true)));
	}
	
}