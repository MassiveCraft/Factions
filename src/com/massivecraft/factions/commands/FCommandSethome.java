package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Role;

public class FCommandSethome extends FCommand
{
	public FCommandSethome()
	{
		this.aliases.add("sethome");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("faction tag", "mine");
		
		this.permission = Permission.COMMAND_SETHOME.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}
		
		if ( ! Conf.homesEnabled)
		{
			fme.sendMessageParsed("<b>Sorry, Faction homes are disabled on this server.");
			return;
		}
		
		Faction faction = this.argAsFaction(0, myFaction);
		if (faction == null) return;
		
		// Can the player set the home for this faction?
		if (faction == myFaction)
		{
			if ( ! Permission.COMMAND_SETHOME_ANY.has(sender) && ! assertMinRole(Role.MODERATOR)) return;
		}
		else
		{
			if (Permission.COMMAND_SETHOME_ANY.has(sender, true)) return;
		}
		
		// Can the player set the faction home HERE?
		if
		(
			! Permission.COMMAND_BYPASS.has(me)
			&&
			Conf.homesMustBeInClaimedTerritory
			&& 
			Board.getFactionAt(new FLocation(me)) != faction
		)
		{
			fme.sendMessageParsed("<b>Sorry, your faction home can only be set inside your own claimed territory.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if ( ! payForCommand(Conf.econCostSethome)) return;

		faction.setHome(me.getLocation());
		
		faction.sendMessage(fme.getNameAndRelevant(myFaction)+"<i> set the home for your faction. You can now use:");
		faction.sendMessage(new FCommandHome().getUseageTemplate());
		if (faction != myFaction)
		{
			fme.sendMessageParsed("<b>You have set the home for the "+faction.getTag(fme)+"<i> faction.");
		}
	}
	
}
