package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsJoin extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsJoin()
	{
		// Aliases
		this.addAliases("join");

		// Args
		this.addRequiredArg("faction");
		this.addOptionalArg("player", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.JOIN.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get());
		if (faction == null) return;

		MPlayer mplayer = this.arg(1, ARMPlayer.getAny(), usender);
		if (mplayer == null) return;
		Faction mplayerFaction = mplayer.getFaction();
		
		boolean samePlayer = mplayer == usender;
		
		// Validate
		if (!samePlayer  && ! Perm.JOIN_OTHERS.has(sender, false))
		{
			msg("<b>You do not have permission to move other players into a faction.");
			return;
		}

		if (faction == mplayerFaction)
		{
			msg("<i>%s <i>%s already a member of %s<i>.", mplayer.describeTo(usender, true), (samePlayer ? "are" : "is"), faction.getName(usender));
			return;
		}

		if (MConf.get().factionMemberLimit > 0 && faction.getMPlayers().size() >= MConf.get().factionMemberLimit)
		{
			msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getName(usender), MConf.get().factionMemberLimit, mplayer.describeTo(usender, false));
			return;
		}

		if (mplayerFaction.isNormal())
		{
			msg("<b>%s must leave %s current faction first.", mplayer.describeTo(usender, true), (samePlayer ? "your" : "their"));
			return;
		}

		if (!MConf.get().canLeaveWithNegativePower && mplayer.getPower() < 0)
		{
			msg("<b>%s cannot join a faction with a negative power level.", mplayer.describeTo(usender, true));
			return;
		}

		if( ! (faction.isOpen() || faction.isInvited(mplayer) || usender.isUsingAdminMode() || Perm.JOIN_ANY.has(sender, false)))
		{
			msg("<i>This faction requires invitation.");
			if (samePlayer)
			{
				faction.msg("%s<i> tried to join your faction.", mplayer.describeTo(faction, true));
			}
			return;
		}

		// Event
		EventFactionsMembershipChange membershipChangeEvent = new EventFactionsMembershipChange(sender, usender, faction, MembershipChangeReason.JOIN);
		membershipChangeEvent.run();
		if (membershipChangeEvent.isCancelled()) return;
		
		// Inform
		if (!samePlayer)
		{
			mplayer.msg("<i>%s <i>moved you into the faction %s<i>.", usender.describeTo(mplayer, true), faction.getName(mplayer));
		}
		faction.msg("<i>%s <i>joined <lime>your faction<i>.", mplayer.describeTo(faction, true));
		usender.msg("<i>%s <i>successfully joined %s<i>.", mplayer.describeTo(usender, true), faction.getName(usender));
		
		// Apply
		mplayer.resetFactionData();
		mplayer.setFaction(faction);
	    
		faction.setInvited(mplayer, false);

		// Derplog
		if (MConf.get().logFactionJoin)
		{
			if (samePlayer)
			{
				Factions.get().log(Txt.parse("%s joined the faction %s.", mplayer.getName(), faction.getName()));
			}
			else
			{
				Factions.get().log(Txt.parse("%s moved the player %s into the faction %s.", usender.getName(), mplayer.getName(), faction.getName()));
			}
		}
	}
	
}
