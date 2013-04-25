package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
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
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.LEADER.node));
	}
	
	@Override
	public void perform()
	{
		UPlayer newLeader = this.arg(0, ARUPlayer.getStartAny(sender));
		if (newLeader == null) return;
		
		Faction targetFaction = this.arg(1, ARFaction.get(sender), usenderFaction);
		if (targetFaction == null) return;
		
		UPlayer targetFactionCurrentLeader = targetFaction.getLeader();
		
		// We now have uplayer and the target faction
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
			FactionsEventMembershipChange event = new FactionsEventMembershipChange(sender, newLeader, targetFaction, MembershipChangeReason.LEADER);
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
		for (UPlayer uplayer : UPlayerColls.get().get(sender).getAllOnline())
		{
			uplayer.msg("%s<i> gave %s<i> the leadership of %s<i>.", senderIsConsole ? "A server admin" : RelationUtil.describeThatToMe(usender, uplayer, true), newLeader.describeTo(uplayer), targetFaction.describeTo(uplayer));
		}
	}
}
