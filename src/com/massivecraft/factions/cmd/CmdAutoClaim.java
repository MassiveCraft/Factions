package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.struct.Permission;

public class CmdAutoClaim extends FCommand
{
	public CmdAutoClaim()
	{
		super();
		this.aliases.add("autoclaim");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flip");
		
		this.permission = Permission.AUTOCLAIM.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		boolean enabled = this.argAsBool(0, ! fme.isAutoClaimEnabled());
		
		fme.setIsAutoClaimEnabled(enabled);

		if ( ! enabled)
		{
			msg("<i>Auto-claiming of land disabled.");
			return;
		}

		FLocation flocation = new FLocation(fme);

		if (Conf.worldsNoClaiming.contains(flocation.getWorldName()))
		{
			msg("<b>Sorry, this world has land claiming disabled.");
			fme.setIsAutoClaimEnabled(false);
			return;
		}

		if (myFaction.getLandRounded() >= myFaction.getPowerRounded())
		{
			msg("<b>You can't claim more land! You need more power!");
			fme.setIsAutoClaimEnabled(false);
			return;
		}

		msg("<i>Auto-claiming of land enabled.");
		fme.attemptClaim(false);
	}
	
}
