package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class CmdAdmin extends FCommand
{	
	public CmdAdmin()
	{
		super();
		this.aliases.add("admin");
		
		this.requiredArgs.add("player name");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.ADMIN.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = true;
	}
	
	@Override
	public void perform()
	{
		FPlayer fyou = this.argAsBestFPlayerMatch(0);
		if (fyou == null) return;
		
		if (fyou.getFaction() != myFaction)
		{
			msg("%s<i> is not a member in your faction.", fyou.getNameAndRelevant(fme));
			return;
		}
		
		if (fyou == fme)
		{
			msg("<b>The target player musn't be yourself.");
			return;
		}
		
		fme.setRole(Role.MODERATOR);
		fyou.setRole(Role.ADMIN);
		
		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			if (fplayer.getFaction() == myFaction)
			{
				fplayer.msg("%s<i> gave %s<i> the leadership of your faction.", fme.getNameAndRelevant(fme), fyou.getNameAndRelevant(fme));
			}
			else
			{
				fplayer.msg("%s<i> gave %s<i> the leadership of %s", fme.getNameAndRelevant(fplayer), fyou.getNameAndRelevant(fplayer), myFaction.getTag(fplayer));
			}
		}
	}
	
}
