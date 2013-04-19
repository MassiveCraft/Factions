package com.massivecraft.factions.cmd;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFPlayer;
import com.massivecraft.factions.event.FactionsEventMembershipChange;
import com.massivecraft.factions.event.FactionsEventMembershipChange.MembershipChangeReason;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsKick extends FCommand
{
	
	public CmdFactionsKick()
	{
		this.addAliases("kick");
		
		this.addRequiredArg("player");
		
		this.addRequirements(ReqHasPerm.get(Perm.KICK.node));
	}
	
	@Override
	public void perform()
	{
		// Arg
		FPlayer fplayer = this.arg(1, ARFPlayer.getStartAny());
		if (fplayer == null) return;
		
		// Validate
		if (fme == fplayer)
		{
			msg("<b>You cannot kick yourself.");
			msg("<i>You might want to: %s", Factions.get().getOuterCmdFactions().cmdFactionsLeave.getUseageTemplate(false));
			return;
		}
		
		if (fplayer.getRole() == Rel.LEADER && !(this.senderIsConsole || fme.isUsingAdminMode()))
		{
			msg("<b>The leader can not be kicked.");
			return;
		}

		if ( ! ConfServer.canLeaveWithNegativePower && fplayer.getPower() < 0)
		{
			msg("<b>You cannot kick that member until their power is positive.");
			return;
		}
		
		// FPerm
		Faction fplayerFaction = fplayer.getFaction();
		if (!FPerm.KICK.has(sender, fplayerFaction)) return;

		// Event
		FactionsEventMembershipChange event = new FactionsEventMembershipChange(sender, fplayer, FactionColl.get().getNone(), MembershipChangeReason.KICK);
		event.run();
		if (event.isCancelled()) return;

		// Inform
		fplayerFaction.msg("%s<i> kicked %s<i> from the faction! :O", fme.describeTo(fplayerFaction, true), fplayer.describeTo(fplayerFaction, true));
		fplayer.msg("%s<i> kicked you from %s<i>! :O", fme.describeTo(fplayer, true), fplayerFaction.describeTo(fplayer));
		if (fplayerFaction != myFaction)
		{
			fme.msg("<i>You kicked %s<i> from the faction %s<i>!", fplayer.describeTo(fme), fplayerFaction.describeTo(fme));
		}

		if (ConfServer.logFactionKick)
		{
			Factions.get().log((senderIsConsole ? "A console command" : fme.getName())+" kicked "+fplayer.getName()+" from the faction: "+fplayerFaction.getTag());
		}

		// Apply
		if (fplayer.getRole() == Rel.LEADER)
		{
			fplayerFaction.promoteNewLeader();
		}
		fplayerFaction.setInvited(fplayer, false);
		fplayer.resetFactionData();
	}
	
}
