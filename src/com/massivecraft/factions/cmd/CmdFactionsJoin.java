package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsJoin extends FCommand
{
	public CmdFactionsJoin()
	{
		this.addAliases("join");
		
		this.addRequiredArg("faction");
		this.addOptionalArg("player", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.JOIN.node));
	}
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get(sender));
		if (faction == null) return;

		FPlayer fplayer = this.arg(1, ARFPlayer.getStartAny(sender), fme);
		if (fplayer == null) return;
		
		boolean samePlayer = fplayer == fme;
		
		// Validate
		if (!samePlayer  && ! Perm.JOIN_OTHERS.has(sender, false))
		{
			msg("<b>You do not have permission to move other players into a faction.");
			return;
		}

		if (faction == fplayer.getFaction())
		{
			msg("<b>%s %s already a member of %s", fplayer.describeTo(fme, true), (samePlayer ? "are" : "is"), faction.getTag(fme));
			return;
		}

		if (UConf.get(faction).factionMemberLimit > 0 && faction.getFPlayers().size() >= UConf.get(faction).factionMemberLimit)
		{
			msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getTag(fme), UConf.get(faction).factionMemberLimit, fplayer.describeTo(fme, false));
			return;
		}

		if (fplayer.hasFaction())
		{
			msg("<b>%s must leave %s current faction first.", fplayer.describeTo(fme, true), (samePlayer ? "your" : "their"));
			return;
		}

		if (!UConf.get(faction).canLeaveWithNegativePower && fplayer.getPower() < 0)
		{
			msg("<b>%s cannot join a faction with a negative power level.", fplayer.describeTo(fme, true));
			return;
		}

		if( ! (faction.isOpen() || faction.isInvited(fplayer) || fme.isUsingAdminMode() || Perm.JOIN_ANY.has(sender, false)))
		{
			msg("<i>This faction requires invitation.");
			if (samePlayer)
			{
				faction.msg("%s<i> tried to join your faction.", fplayer.describeTo(faction, true));
			}
			return;
		}

		// Event
		FactionsEventMembershipChange membershipChangeEvent = new FactionsEventMembershipChange(sender, fme, faction, MembershipChangeReason.JOIN);
		membershipChangeEvent.run();
		if (membershipChangeEvent.isCancelled()) return;
		
		// Inform
		if (!samePlayer)
		{
			fplayer.msg("<i>%s moved you into the faction %s.", fme.describeTo(fplayer, true), faction.getTag(fplayer));
		}
		faction.msg("<i>%s joined your faction.", fplayer.describeTo(faction, true));
		fme.msg("<i>%s successfully joined %s.", fplayer.describeTo(fme, true), faction.getTag(fme));
		
		// Apply
		fplayer.resetFactionData();
		fplayer.setFaction(faction);
	    
		faction.setInvited(fplayer, false);

		// Derplog
		if (MConf.get().logFactionJoin)
		{
			if (samePlayer)
				Factions.get().log("%s joined the faction %s.", fplayer.getName(), faction.getTag());
			else
				Factions.get().log("%s moved the player %s into the faction %s.", fme.getName(), fplayer.getName(), faction.getTag());
		}
	}
}
