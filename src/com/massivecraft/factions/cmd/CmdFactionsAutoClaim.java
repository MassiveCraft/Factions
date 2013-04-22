package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.ps.PS;

public class CmdFactionsAutoClaim extends FCommand
{
	public CmdFactionsAutoClaim()
	{
		this.addAliases("autoclaim");
		
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.AUTOCLAIM.node));
		this.addRequirements(ReqIsPlayer.get());
	}

	@Override
	public void perform()
	{
		Faction forFaction = this.arg(0, ARFaction.get(myFaction), myFaction);
		if (forFaction == null || forFaction == fme.getAutoClaimFor())
		{
			fme.setAutoClaimFor(null);
			msg("<i>Auto-claiming of land disabled.");
			return;
		}
		
		if ( ! FPerm.TERRITORY.has(fme, forFaction, true)) return;
		
		fme.setAutoClaimFor(forFaction);
		
		msg("<i>Now auto-claiming land for <h>%s<i>.", forFaction.describeTo(fme));
		fme.attemptClaim(forFaction, PS.valueOf(me), true);
	}
	
}