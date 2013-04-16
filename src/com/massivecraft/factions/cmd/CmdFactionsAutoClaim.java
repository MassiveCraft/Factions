package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;

public class CmdFactionsAutoClaim extends FCommand
{
	public CmdFactionsAutoClaim()
	{
		super();
		this.aliases.add("autoclaim");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		
		this.permission = Perm.AUTOCLAIM.node;
		
		this.addRequirements(ReqIsPlayer.get());
	}

	@Override
	public void perform()
	{
		Faction forFaction = this.argAsFaction(0, myFaction);
		if (forFaction == null || forFaction == fme.getAutoClaimFor())
		{
			fme.setAutoClaimFor(null);
			msg("<i>Auto-claiming of land disabled.");
			return;
		}
		
		if ( ! FPerm.TERRITORY.has(fme, forFaction, true)) return;
		
		fme.setAutoClaimFor(forFaction);
		
		msg("<i>Now auto-claiming land for <h>%s<i>.", forFaction.describeTo(fme));
		fme.attemptClaim(forFaction, me.getLocation(), true);
	}
	
}