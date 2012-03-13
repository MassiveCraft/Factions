package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.FPlayerJoinEvent;
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
		if (this.senderIsConsole || fme.hasAdminMode() || Permission.LEADER_ANY.has(sender, false))
		{
			// Do whatever you wish
		}
		else
		{
			// Follow the standard rules
			if (fme.getRole() != Rel.LEADER || targetFaction != myFaction)
			{
				sender.sendMessage(p.txt.parse("<b>You must be leader of the faction to %s.", this.getHelpShort()));
				return;
			}
			
			if (newLeader.getFaction() != myFaction)
			{
				msg("%s<i> is not a member in the faction.", newLeader.describeTo(fme, true));
				return;
			}
			
			if (newLeader == fme)
			{
				msg("<b>The target player musn't be yourself.");
				return;
			}
		}

		// only perform a FPlayerJoinEvent when newLeader isn't actually in the faction
		if (newLeader.getFaction() != targetFaction)
		{
			FPlayerJoinEvent event = new FPlayerJoinEvent(FPlayers.i.get(me),targetFaction,FPlayerJoinEvent.PlayerJoinReason.LEADER);
			Bukkit.getServer().getPluginManager().callEvent(event);
			if (event.isCancelled()) return;
		}

		// if target player is currently leader, demote and replace him
		if (targetFactionCurrentLeader == newLeader)
		{
			targetFaction.promoteNewLeader();
			msg("<i>You have demoted %s<i> from the position of faction leader.", newLeader.describeTo(fme, true));
			newLeader.msg("<i>You have been demoted from the position of faction leader by %s<i>.", senderIsConsole ? "a server admin" : fme.describeTo(newLeader, true));
			return;
		}

		// Perform the switching
		if (targetFactionCurrentLeader != null)
		{
			targetFactionCurrentLeader.setRole(Rel.OFFICER);
		}
		newLeader.setFaction(targetFaction);
		newLeader.setRole(Rel.LEADER);
		msg("<i>You have promoted %s<i> to the position of faction leader.", newLeader.describeTo(fme, true));
		
		// Inform all players
		for (FPlayer fplayer : FPlayers.i.getOnline())
		{
			fplayer.msg("%s<i> gave %s<i> the leadership of %s<i>.", senderIsConsole ? "A server admin" : RelationUtil.describeThatToMe(fme, fplayer, true), newLeader.describeTo(fplayer), targetFaction.describeTo(fplayer));
		}
	}
}
