package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.event.EventFactionsDisband;
import com.massivecraft.factions.event.EventFactionsMembershipChange;
import com.massivecraft.factions.event.EventFactionsMembershipChange.MembershipChangeReason;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsDisband extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsDisband()
	{
		// Aliases
		this.addAliases("disband");

		// Args
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.DISBAND.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{	
		// Args
		Faction faction = this.arg(0, ARFaction.get(), usenderFaction);
		if (faction == null) return;
		
		// FPerm
		if ( ! FPerm.DISBAND.has(usender, faction, true)) return;

		// Verify
		if (faction.getFlag(FFlag.PERMANENT))
		{
			msg("<i>This faction is designated as permanent, so you cannot disband it.");
			return;
		}

		// Event
		EventFactionsDisband event = new EventFactionsDisband(me, faction);
		event.run();
		if (event.isCancelled()) return;

		// Merged Apply and Inform
		
		// Run event for each player in the faction
		for (MPlayer mplayer : faction.getMPlayers())
		{
			EventFactionsMembershipChange membershipChangeEvent = new EventFactionsMembershipChange(sender, mplayer, FactionColl.get().getNone(), MembershipChangeReason.DISBAND);
			membershipChangeEvent.run();
		}

		// Inform all players
		for (MPlayer mplayer : MPlayerColl.get().getAllOnline())
		{
			String who = usender.describeTo(mplayer);
			if (mplayer.getFaction() == faction)
			{
				mplayer.msg("<h>%s<i> disbanded your faction.", who);
			}
			else
			{
				mplayer.msg("<h>%s<i> disbanded the faction %s.", who, faction.getName(mplayer));
			}
		}
		
		if (MConf.get().logFactionDisband)
		{
			Factions.get().log(Txt.parse("<i>The faction <h>%s <i>(<h>%s<i>) was disbanded by <h>%s<i>.", faction.getName(), faction.getId(), usender.getDisplayName(IdUtil.getConsole())));
		}		
		
		faction.detach();
	}
	
}
