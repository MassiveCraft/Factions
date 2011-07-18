package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

public class FCommandSethome extends FBaseCommand {
	
	public FCommandSethome() {
		aliases.add("sethome");
		
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
		
		if (Conf.homesMustBeInClaimedTerritory && !me.isInOwnTerritory()) {
			me.sendMessage("Sorry, your faction home can only be set inside your own claimed territory.");
			return;
		}
		
		Faction myFaction = me.getFaction();
		myFaction.setHome(player.getLocation());
		
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" set the home for your faction. You can now use:");
		myFaction.sendMessage(new FCommandHome().getUseageTemplate());
	}
	
}
