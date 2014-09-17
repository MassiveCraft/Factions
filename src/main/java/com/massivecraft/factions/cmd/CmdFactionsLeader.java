package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.util.RelationUtil;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsLeader extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	public CmdFactionsLeader()
	{
		// Aliases
		this.addAliases("leader");

		// Args
		this.addRequiredArg("player");
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.LEADER.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		MPlayer newLeader = this.arg(0, ARMPlayer.getAny());
		if (newLeader == null) return;
		
		Faction targetFaction = this.arg(1, ARFaction.get(), usenderFaction);
		if (targetFaction == null) return;
		
		MPlayer targetFactionCurrentLeader = targetFaction.getLeader();
		
		// We now have mplayer and the target faction
		if (this.senderIsConsole || usender.isUsingAdminMode() || Perm.LEADER_ANY.has(sender, false))
		{
			// Do whatever you wish
		}
		else
		{
			// Follow the standard rules
			if (usender.getRole() != Rel.LEADER || targetFaction != usenderFaction)
			{
				sender.sendMessage(Txt.parse("<b>You must be leader of the faction to %s.", this.getDesc()));
				return;
			}
			
			if (newLeader.getFaction() != usenderFaction)
			{
				msg("%s<i> is not a member in the faction.", newLeader.describeTo(usender, true));
				return;
			}
			
			if (newLeader == usender)
			{
				msg("<b>The target player musn't be yourself.");
				return;
			}
		}

		// only run event when newLeader isn't actually in the faction
		if (newLeader.getFaction() != targetFaction)
		{
			EventFactionsMembershipChange event = new EventFactionsMembershipChange(sender, newLeader, targetFaction, MembershipChangeReason.LEADER);
			event.run();
			if (event.isCancelled()) return;
		}

		// if target player is currently leader, demote and replace him
		if (targetFactionCurrentLeader == newLeader)
		{
			targetFaction.promoteNewLeader();
			msg("<i>You have demoted %s<i> from the position of faction leader.", newLeader.describeTo(usender, true));
			newLeader.msg("<i>You have been demoted from the position of faction leader by %s<i>.", usender.describeTo(newLeader, true));
			return;
		}

		// Perform the switching
		if (targetFactionCurrentLeader != null)
		{
			targetFactionCurrentLeader.setRole(Rel.OFFICER);
		}
		newLeader.setFaction(targetFaction);
		newLeader.setRole(Rel.LEADER);
		msg("<i>You have promoted %s<i> to the position of faction leader.", newLeader.describeTo(usender, true));
		
		// Inform all players
		for (MPlayer mplayer : MPlayerColl.get().getAllOnline())
		{
			mplayer.msg("%s<i> gave %s<i> the leadership of %s<i>.", senderIsConsole ? "A server admin" : RelationUtil.describeThatToMe(usender, mplayer, true), newLeader.describeTo(mplayer), targetFaction.describeTo(mplayer));
		}
	}
	
}
