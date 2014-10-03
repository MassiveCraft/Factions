package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.IdUtil;

public class CmdFactionsKick extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsKick()
	{
		// Aliases
		this.addAliases("kick");

		// Args
		this.addRequiredArg("player");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.KICK.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Arg
		MPlayer mplayer = this.arg(0, ARMPlayer.getAny());
		if (mplayer == null) return;
		
		// Validate
		if (msender == mplayer)
		{
			msg("<b>You cannot kick yourself.");
			msg("<i>You might want to: %s", Factions.get().getOuterCmdFactions().cmdFactionsLeave.getUseageTemplate(false));
			return;
		}
		
		if (mplayer.getRole() == Rel.LEADER && !(this.senderIsConsole || msender.isUsingAdminMode()))
		{
			msg("<b>The leader can not be kicked.");
			return;
		}

		if ( ! MConf.get().canLeaveWithNegativePower && mplayer.getPower() < 0)
		{
			msg("<b>You cannot kick that member until their power is positive.");
			return;
		}
		
		// MPerm
		Faction mplayerFaction = mplayer.getFaction();
		if ( ! MPerm.getPermKick().has(msender, mplayerFaction, true)) return;

		// Event
		EventFactionsMembershipChange event = new EventFactionsMembershipChange(sender, mplayer, FactionColl.get().getNone(), MembershipChangeReason.KICK);
		event.run();
		if (event.isCancelled()) return;

		// Inform
		mplayerFaction.msg("%s<i> kicked %s<i> from the faction! :O", msender.describeTo(mplayerFaction, true), mplayer.describeTo(mplayerFaction, true));
		mplayer.msg("%s<i> kicked you from %s<i>! :O", msender.describeTo(mplayer, true), mplayerFaction.describeTo(mplayer));
		if (mplayerFaction != msenderFaction)
		{
			msender.msg("<i>You kicked %s<i> from the faction %s<i>!", mplayer.describeTo(msender), mplayerFaction.describeTo(msender));
		}

		if (MConf.get().logFactionKick)
		{
			Factions.get().log(msender.getDisplayName(IdUtil.getConsole()) + " kicked " + mplayer.getName() + " from the faction " + mplayerFaction.getName());
		}

		// Apply
		if (mplayer.getRole() == Rel.LEADER)
		{
			mplayerFaction.promoteNewLeader();
		}
		mplayerFaction.setInvited(mplayer, false);
		mplayer.resetFactionData();
	}
	
}
