package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.ps.PS;

public class CmdFactionsAutoClaim extends FCommand
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
		Faction forFaction = this.arg(0, ARFaction.get(), usenderFaction);
		
		if (forFaction == null || forFaction == usender.getAutoClaimFaction())
		{
			usender.setAutoClaimFaction(null);
			msg("<i>Auto-claiming of land disabled.");
			return;
		}
		
		// FPerm
		if (forFaction.isNormal() && !FPerm.TERRITORY.has(usender, forFaction, true)) return;
		
		usender.setAutoClaimFaction(forFaction);
		
		msg("<i>Now auto-claiming land for <h>%s<i>.", forFaction.describeTo(usender));
		usender.tryClaim(forFaction, PS.valueOf(me), true, true);
	}
	
}