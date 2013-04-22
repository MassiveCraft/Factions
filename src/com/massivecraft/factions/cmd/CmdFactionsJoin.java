package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARUPlayer;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.UPlayer;
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

		UPlayer uplayer = this.arg(1, ARUPlayer.getStartAny(sender), fme);
		if (uplayer == null) return;
		
		boolean samePlayer = uplayer == fme;
		
		// Validate
		if (!samePlayer  && ! Perm.JOIN_OTHERS.has(sender, false))
		{
			msg("<b>You do not have permission to move other players into a faction.");
			return;
		}

		if (faction == uplayer.getFaction())
		{
			msg("<b>%s %s already a member of %s", uplayer.describeTo(fme, true), (samePlayer ? "are" : "is"), faction.getTag(fme));
			return;
		}

		if (UConf.get(faction).factionMemberLimit > 0 && faction.getUPlayers().size() >= UConf.get(faction).factionMemberLimit)
		{
			msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getTag(fme), UConf.get(faction).factionMemberLimit, uplayer.describeTo(fme, false));
			return;
		}

		if (uplayer.hasFaction())
		{
			msg("<b>%s must leave %s current faction first.", uplayer.describeTo(fme, true), (samePlayer ? "your" : "their"));
			return;
		}

		if (!UConf.get(faction).canLeaveWithNegativePower && uplayer.getPower() < 0)
		{
			msg("<b>%s cannot join a faction with a negative power level.", uplayer.describeTo(fme, true));
			return;
		}

		if( ! (faction.isOpen() || faction.isInvited(uplayer) || fme.isUsingAdminMode() || Perm.JOIN_ANY.has(sender, false)))
		{
			msg("<i>This faction requires invitation.");
			if (samePlayer)
			{
				faction.msg("%s<i> tried to join your faction.", uplayer.describeTo(faction, true));
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
			uplayer.msg("<i>%s moved you into the faction %s.", fme.describeTo(uplayer, true), faction.getTag(uplayer));
		}
		faction.msg("<i>%s joined your faction.", uplayer.describeTo(faction, true));
		fme.msg("<i>%s successfully joined %s.", uplayer.describeTo(fme, true), faction.getTag(fme));
		
		// Apply
		uplayer.resetFactionData();
		uplayer.setFaction(faction);
	    
		faction.setInvited(uplayer, false);

		// Derplog
		if (MConf.get().logFactionJoin)
		{
			if (samePlayer)
				Factions.get().log("%s joined the faction %s.", uplayer.getName(), faction.getTag());
			else
				Factions.get().log("%s moved the player %s into the faction %s.", fme.getName(), uplayer.getName(), faction.getTag());
		}
	}
}
