package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class FCommandAutoClaim extends FCommand
{
	public FCommandAutoClaim()
	{
		super();
		this.aliases.add("autoclaim");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flipp");
		
		this.permission = Permission.COMMAND_AUTOCLAIM.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
	}

	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}

		boolean enabled = this.argAsBool(0, ! fme.isAutoClaimEnabled());
		
		fme.setIsAutoClaimEnabled(enabled);

		if ( ! enabled)
		{
			sendMessageParsed("<i>Auto-claiming of land disabled.");
			return;
		}

		Faction myFaction = fme.getFaction();
		FLocation flocation = new FLocation(fme);

		if (Conf.worldsNoClaiming.contains(flocation.getWorldName()))
		{
			sendMessageParsed("<b>Sorry, this world has land claiming disabled.");
			fme.setIsAutoClaimEnabled(false);
			return;
		}

		if (myFaction.getLandRounded() >= myFaction.getPowerRounded())
		{
			sendMessageParsed("<b>You can't claim more land! You need more power!");
			fme.setIsAutoClaimEnabled(false);
			return;
		}

		sendMessageParsed("<i>Auto-claiming of land enabled.");
		fme.attemptClaim(false);
	}
	
}
