package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.util.RelationUtil;

public class CmdLeader extends FCommand
{	
	public CmdLeader()
	{
		super();
		this.aliases.add("leader");
		
		this.requiredArgs.add("player");
		this.optionalArgs.put("faction", "your");
		
		this.permission = Permission.LEADER.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		FPlayer newLeader = this.argAsBestFPlayerMatch(0);
		if (newLeader == null) return;
		
		Faction targetFaction = this.argAsFaction(1, myFaction);
		if (targetFaction == null) return;
		
		FPlayer targetFactionCurrentLeader = targetFaction.getFPlayerLeader();
		
		// We now have fplayer and the target faction
		if (this.senderIsConsole || fme.hasAdminMode())
		{
			// Do whatever you wish
		}
		else
		{
			// Follow the standard rules
			if (fme.getRole() != Rel.LEADER)
			{
				sender.sendMessage(p.txt.parse("<b>Only faction admins can %s.", this.getHelpShort()));
				return;
			}
			
			if (newLeader.getFaction() != myFaction)
			{
				msg("%s<i> is not a member in your faction.", newLeader.describeTo(fme, true));
				return;
			}
			
			if (newLeader == fme)
			{
				msg("<b>The target player musn't be yourself.");
				return;
			}
		}
		
		// Perform the switching
		if (targetFactionCurrentLeader != null)
		{
			targetFactionCurrentLeader.setRole(Rel.OFFICER);
		}
		newLeader.setFaction(targetFaction);
		newLeader.setRole(Rel.LEADER);
		
		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			fplayer.msg("%s<i> gave %s<i> the leadership of %s", RelationUtil.describeThatToMe(fme, fplayer, true), newLeader.describeTo(fplayer), targetFaction.describeTo(fplayer));
		}
	}
}
