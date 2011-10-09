package com.massivecraft.factions.commands;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class FCommandMod extends FCommand
{
	
	public FCommandMod()
	{
		super();
		this.aliases.add("mod");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_MOD.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;
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
		
		if (you.getFaction() != myFaction)
		{
			sendMessageParsed("%s<b> is not a member in your faction.", you.getNameAndRelevant(fme));
			return;
		}
		
		if (you == fme)
		{
			sendMessageParsed("<b>The target player musn't be yourself.");
			return;
		}

		if (you.getRole() == Role.MODERATOR)
		{
			// Revoke
			you.setRole(Role.NORMAL);
			myFaction.sendMessageParsed("%s<i> is no longer moderator in your faction.", you.getNameAndRelevant(myFaction));
		}
		else
		{
			// Give
			you.setRole(Role.MODERATOR);
			myFaction.sendMessageParsed("%s<i> was promoted to moderator in your faction.", you.getNameAndRelevant(myFaction));
		}
	}
	
}
