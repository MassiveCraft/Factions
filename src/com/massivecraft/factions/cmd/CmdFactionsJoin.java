package com.massivecraft.factions.cmd;

import org.bukkit.Bukkit;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.event.FPlayerJoinEvent;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsJoin extends FCommand
{
	public CmdFactionsJoin()
	{
		super();
		
		this.addAliases("join");
		
		this.requiredArgs.add("faction");
		this.optionalArgs.put("player", "you");
		
		this.addRequirements(ReqHasPerm.get(Perm.JOIN.node));
	}
	
	@Override
	public void perform()
	{
		Faction faction = this.argAsFaction(0);
		if (faction == null) return;

		FPlayer fplayer = this.argAsBestFPlayerMatch(1, fme, false);
		boolean samePlayer = fplayer == fme;

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

		if (ConfServer.factionMemberLimit > 0 && faction.getFPlayers().size() >= ConfServer.factionMemberLimit)
		{
			msg(" <b>!<white> The faction %s is at the limit of %d members, so %s cannot currently join.", faction.getTag(fme), ConfServer.factionMemberLimit, fplayer.describeTo(fme, false));
			return;
		}

		if (fplayer.hasFaction())
		{
			msg("<b>%s must leave %s current faction first.", fplayer.describeTo(fme, true), (samePlayer ? "your" : "their"));
			return;
		}

		if (!ConfServer.canLeaveWithNegativePower && fplayer.getPower() < 0)
		{
			msg("<b>%s cannot join a faction with a negative power level.", fplayer.describeTo(fme, true));
			return;
		}

		if( ! (faction.isOpen() || faction.isInvited(fplayer) || fme.hasAdminMode() || Perm.JOIN_ANY.has(sender, false)))
		{
			msg("<i>This faction requires invitation.");
			if (samePlayer)
				faction.msg("%s<i> tried to join your faction.", fplayer.describeTo(faction, true));
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make sure they can pay
		if (samePlayer && ! canAffordCommand(ConfServer.econCostJoin, "to join a faction")) return;

		// trigger the join event (cancellable)
		FPlayerJoinEvent joinEvent = new FPlayerJoinEvent(FPlayerColl.get().get(me),faction,FPlayerJoinEvent.PlayerJoinReason.COMMAND);
		Bukkit.getServer().getPluginManager().callEvent(joinEvent);
		if (joinEvent.isCancelled()) return;

		// then make 'em pay (if applicable)
		if (samePlayer && ! payForCommand(ConfServer.econCostJoin, "to join a faction", "for joining a faction")) return;

		fme.setRole(ConfServer.factionRankDefault); // They have just joined a faction, start them out on the lowest rank (default config).

		if (!samePlayer)
			fplayer.msg("<i>%s moved you into the faction %s.", fme.describeTo(fplayer, true), faction.getTag(fplayer));
		faction.msg("<i>%s joined your faction.", fplayer.describeTo(faction, true));
		fme.msg("<i>%s successfully joined %s.", fplayer.describeTo(fme, true), faction.getTag(fme));
		
		fplayer.resetFactionData();
		fplayer.setFaction(faction);
	    
		faction.deinvite(fplayer);
		

		if (ConfServer.logFactionJoin)
		{
			if (samePlayer)
				Factions.get().log("%s joined the faction %s.", fplayer.getName(), faction.getTag());
			else
				Factions.get().log("%s moved the player %s into the faction %s.", fme.getName(), fplayer.getName(), faction.getTag());
		}
	}
}
