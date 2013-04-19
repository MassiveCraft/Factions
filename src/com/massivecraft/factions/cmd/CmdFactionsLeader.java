package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.event.FactionsEventJoin;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.util.Txt;

public class CmdFactionsLeader extends FCommand
{	
	public CmdFactionsLeader()
	{
		this.addAliases("leader");
		
		this.addRequiredArg("player");
		this.addOptionalArg("faction", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.LEADER.node));
	}
	
	@Override
	public void perform()
	{
		FPlayer newLeader = this.arg(0, ARFPlayer.getStartAny());
		if (newLeader == null) return;
		
		Faction targetFaction = this.arg(1, ARFaction.get(), myFaction);
		if (targetFaction == null) return;
		
		FPlayer targetFactionCurrentLeader = targetFaction.getLeader();
		
		// We now have fplayer and the target faction
		if (this.senderIsConsole || fme.isUsingAdminMode() || Perm.LEADER_ANY.has(sender, false))
		{
			// Do whatever you wish
		}
		else
		{
			// Follow the standard rules
			if (fme.getRole() != Rel.LEADER || targetFaction != myFaction)
			{
				sender.sendMessage(Txt.parse("<b>You must be leader of the faction to %s.", this.getDesc()));
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
			FactionsEventJoin event = new FactionsEventJoin(sender, newLeader, targetFaction, FactionsEventJoin.PlayerJoinReason.LEADER);
			event.run();
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
		for (FPlayer fplayer : FPlayerColl.get().getAllOnline())
		{
			fplayer.msg("%s<i> gave %s<i> the leadership of %s<i>.", senderIsConsole ? "A server admin" : RelationUtil.describeThatToMe(fme, fplayer, true), newLeader.describeTo(fplayer), targetFaction.describeTo(fplayer));
		}
	}
}
