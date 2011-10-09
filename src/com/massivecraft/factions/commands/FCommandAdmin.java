package com.massivecraft.factions.commands;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class FCommandAdmin extends FCommand
{	
	public FCommandAdmin()
	{
		super();
		this.aliases.add("admin");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.COMMAND_ADMIN.node;
		
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
		
		FPlayer fyou = this.argAsBestFPlayerMatch(0);
		if (fyou == null) return;
		
		Faction myFaction = fme.getFaction();
		
		if (fyou.getFaction() != myFaction)
		{
			sendMessageParsed("%s<i> is not a member in your faction.", fyou.getNameAndRelevant(fme));
			return;
		}
		
		if (fyou == fme)
		{
			sendMessageParsed("<b>The target player musn't be yourself.");
			return;
		}
		
		fme.setRole(Role.MODERATOR);
		fyou.setRole(Role.ADMIN);
		
		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			if (fplayer.getFaction() == myFaction)
			{
				fplayer.sendMessageParsed("%s<i> gave %s<i> the leadership of your faction.", fme.getNameAndRelevant(fme), fyou.getNameAndRelevant(fme));
			}
			else
			{
				fplayer.sendMessageParsed("%s<i> gave %s<i> the leadership of %s", fme.getNameAndRelevant(fplayer), fyou.getNameAndRelevant(fplayer), myFaction.getTag(fplayer));
			}
		}
	}
	
}
