package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;

public class FCommandInvite extends FCommand
{
	public FCommandInvite()
	{
		super();
		this.aliases.add("invite");
		this.aliases.add("inv");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_INVITE.node;
		
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
			sendMessageParsed("<i>You might want to: " + new FCommandKick().getUseageTemplate(false));
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostInvite))
		{
			return;
		}

		myFaction.invite(you);
		
		you.sendMessageParsed("%s<i> invited you to %s", fme.getNameAndRelevant(you), myFaction.getTag(you));
		myFaction.sendMessageParsed("%s<i> invited %s<i> to your faction.", fme.getNameAndRelevant(fme), you.getNameAndRelevant(fme));
	}
	
}
