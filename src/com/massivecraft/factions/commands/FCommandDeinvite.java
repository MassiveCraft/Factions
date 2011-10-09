package com.massivecraft.factions.commands;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class FCommandDeinvite extends FCommand
{
	
	public FCommandDeinvite()
	{
		super();
		this.aliases.add("deinvite");
		this.aliases.add("deinv");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_DEINVITE.node;
		
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
		
		FPlayer you = this.argAsBestFPlayerMatch(0);
		if (you == null) return;
		
		Faction myFaction = fme.getFaction();
		
		if (you.getFaction() == myFaction)
		{
			sendMessageParsed("%s<i> is already a member of %s", you.getName(), myFaction.getTag());
			sendMessageParsed("<i>You might want to: %s", new FCommandKick().getUseageTemplate(false));
			return;
		}
		
		myFaction.deinvite(you);
		
		you.sendMessageParsed("%s<i> revoked your invitation to %s", fme.getNameAndRelevant(you), myFaction.getTag(you));
		myFaction.sendMessageParsed("%s<i> revoked %s's<i> invitation.", fme.getNameAndRelevant(fme), you.getNameAndRelevant(fme));
	}
	
}
