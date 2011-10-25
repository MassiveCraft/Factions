package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Permission;

public class CmdSethome extends FCommand
{
	public CmdSethome()
	{
		this.aliases.add("sethome");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction", "your");
		
		this.permission = Permission.SETHOME.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeOfficer = false;
		senderMustBeLeader = false;
	}
	
	@Override
	public void perform()
	{
		if ( ! Conf.homesEnabled)
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
			Conf.homesMustBeInClaimedTerritory
			&& 
			Board.getFactionAt(new FLocation(me)) != faction
		)
		{
			fme.msg("<b>Sorry, your faction home can only be set inside your own claimed territory.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostSethome, "to set the faction home", "for setting the faction home")) return;

		faction.setHome(me.getLocation());
		
		faction.msg("%s<i> set the home for your faction. You can now use:", fme.describeTo(myFaction, true));
		faction.sendMessage(p.cmdBase.cmdHome.getUseageTemplate());
		if (faction != myFaction)
		{
			fme.msg("<b>You have set the home for the "+faction.getTag(fme)+"<i> faction.");
		}
	}
	
}
