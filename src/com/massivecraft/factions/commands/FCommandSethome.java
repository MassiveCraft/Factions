package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Role;

public class FCommandSethome extends FBaseCommand {
	
	public FCommandSethome() {
		aliases.add("sethome");
		
		optionalParameters.add("faction tag");
		
		helpDescription = "Set the faction home";
	}
	
	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		if ( ! Conf.homesEnabled) {
			me.sendMessage("Sorry, Faction homes are disabled on this server.");
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if (parameters.size() > 0) {
			if (!Factions.hasPermAdminBypass(player)) {
				me.sendMessage("You cannot set the home of another faction without adminBypass permission.");
				return;
			}
			
			myFaction = findFaction(parameters.get(0), true);
			
			if (myFaction == null) {
				me.sendMessage("No such faction seems to exist.");
				return;
			}
		}
		
		if (Conf.homesMustBeInClaimedTerritory && !me.isInOwnTerritory() && !Factions.hasPermAdminBypass(player)) {
			me.sendMessage("Sorry, your faction home can only be set inside your own claimed territory.");
			return;
		}

		// if economy is enabled, they're not on the bypass list, and this command has a cost set, make 'em pay
		if (!payForCommand(Conf.econCostSethome)) {
			return;
		}

		myFaction.setHome(player.getLocation());
		
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" set the home for your faction. You can now use:");
		myFaction.sendMessage(new FCommandHome().getUseageTemplate());
		if (myFaction != me.getFaction()) {
			me.sendMessage("You have set the home for the "+myFaction.getTag(me)+Conf.colorSystem+" faction.");
		}
	}
	
}
