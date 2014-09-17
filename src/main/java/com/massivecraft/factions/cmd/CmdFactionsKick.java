package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.IdUtil;

public class CmdFactionsKick extends FCommand
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
		if (usender == mplayer)
		{
			msg("<b>You cannot kick yourself.");
			msg("<i>You might want to: %s", Factions.get().getOuterCmdFactions().cmdFactionsLeave.getUseageTemplate(false));
			return;
		}
		
		if (mplayer.getRole() == Rel.LEADER && !(this.senderIsConsole || usender.isUsingAdminMode()))
		{
			msg("<b>The leader can not be kicked.");
			return;
		}

		if ( ! MConf.get().canLeaveWithNegativePower && mplayer.getPower() < 0)
		{
			msg("<b>You cannot kick that member until their power is positive.");
			return;
		}
		
		// FPerm
		Faction mplayerFaction = mplayer.getFaction();
		if (!FPerm.KICK.has(usender, mplayerFaction, true)) return;

		// Event
		EventFactionsMembershipChange event = new EventFactionsMembershipChange(sender, mplayer, FactionColl.get().getNone(), MembershipChangeReason.KICK);
		event.run();
		if (event.isCancelled()) return;

		// Inform
		mplayerFaction.msg("%s<i> kicked %s<i> from the faction! :O", usender.describeTo(mplayerFaction, true), mplayer.describeTo(mplayerFaction, true));
		mplayer.msg("%s<i> kicked you from %s<i>! :O", usender.describeTo(mplayer, true), mplayerFaction.describeTo(mplayer));
		if (mplayerFaction != usenderFaction)
		{
			usender.msg("<i>You kicked %s<i> from the faction %s<i>!", mplayer.describeTo(usender), mplayerFaction.describeTo(usender));
		}

		if (MConf.get().logFactionKick)
		{
			Factions.get().log(usender.getDisplayName(IdUtil.getConsole()) + " kicked " + mplayer.getName() + " from the faction " + mplayerFaction.getName());
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
