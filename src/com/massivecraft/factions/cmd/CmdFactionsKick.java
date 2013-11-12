package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsKick extends FCommand
{
	
	public CmdFactionsKick()
	{
		this.addAliases("kick");
		
		this.addRequiredArg("player");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.KICK.node));
	}
	
	@Override
	public void perform()
	{
		// Arg
		UPlayer uplayer = this.arg(0, ARUPlayer.getStartAny(sender));
		if (uplayer == null) return;
		
		// Validate
		if (usender == uplayer)
		{
			msg("<b>你不能移除自己.");
			msg("<i>你可能要: %s", Factions.get().getOuterCmdFactions().cmdFactionsLeave.getUseageTemplate(false));
			return;
		}
		
		if (uplayer.getRole() == Rel.LEADER && !(this.senderIsConsole || usender.isUsingAdminMode()))
		{
			msg("<b>会长不能被移除.");
			return;
		}

		if ( ! UConf.get(uplayer).canLeaveWithNegativePower && uplayer.getPower() < 0)
		{
			msg("<b>You cannot kick that member until their power is positive.");
			return;
		}
		
		// FPerm
		Faction uplayerFaction = uplayer.getFaction();
		if (!FPerm.KICK.has(usender, uplayerFaction, true)) return;

		// Event
		FactionsEventMembershipChange event = new FactionsEventMembershipChange(sender, uplayer, FactionColls.get().get(uplayer).getNone(), MembershipChangeReason.KICK);
		event.run();
		if (event.isCancelled()) return;

		// Inform
		uplayerFaction.msg("%s<i> 把 %s<i> 踢出了工会! :O", usender.describeTo(uplayerFaction, true), uplayer.describeTo(uplayerFaction, true));
		uplayer.msg("%s<i> 把你从 %s<i> 工会里除名啦! :O", usender.describeTo(uplayer, true), uplayerFaction.describeTo(uplayer));
		if (uplayerFaction != usenderFaction)
		{
			usender.msg("<i>你把 %s<i> 从 %s<i> 工会里踢了出去!", uplayer.describeTo(usender), uplayerFaction.describeTo(usender));
		}

		if (MConf.get().logFactionKick)
		{
			Factions.get().log(usender.getDisplayName() + " kicked " + uplayer.getName() + " from the faction " + uplayerFaction.getName());
		}

		// Apply
		if (uplayer.getRole() == Rel.LEADER)
		{
			uplayerFaction.promoteNewLeader();
		}
		uplayerFaction.setInvited(uplayer, false);
		uplayer.resetFactionData();
	}
	
}
