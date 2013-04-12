package com.massivecraft.factions.cmd;

import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Perm;
import com.massivecraft.mcore.ps.PS;

public class CmdFactionsSethome extends FCommand
{
	public CmdFactionsSethome()
	{
		this.aliases.add("sethome");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		
		this.permission = Perm.SETHOME.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! ConfServer.homesEnabled)
		{
			fme.msg("<b>Sorry, Faction homes are disabled on this server.");
			return;
		}
		
		Faction faction = this.argAsFaction(0, myFaction);
		if (faction == null) return;
		
		// Can the player set the home for this faction?
		if ( ! FPerm.SETHOME.has(sender, faction, true)) return;
		
		// Can the player set the faction home HERE?
		if
		(
			! fme.hasAdminMode()
			&&
			ConfServer.homesMustBeInClaimedTerritory
			&& 
			BoardColl.get().getFactionAt(PS.valueOf(me)) != faction
		)
		{
			fme.msg("<b>Sorry, your faction home can only be set inside your own claimed territory.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(ConfServer.econCostSethome, "to set the faction home", "for setting the faction home")) return;

		faction.setHome(me.getLocation());
		
		faction.msg("%s<i> set the home for your faction. You can now use:", fme.describeTo(myFaction, true));
		faction.sendMessage(p.cmdBase.cmdFactionsHome.getUseageTemplate());
		if (faction != myFaction)
		{
			fme.msg("<b>You have set the home for the "+faction.getTag(fme)+"<i> faction.");
		}
	}
	
}
