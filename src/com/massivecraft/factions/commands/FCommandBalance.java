package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Econ;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;


public class FCommandBalance extends FBaseCommand {
	
	public FCommandBalance() {
		aliases.add("balance");
		aliases.add("money");
		
		optionalParameters.add("faction tag");
		
		helpDescription = "Show faction's current balance";
	}
	
	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if (!Conf.bankEnabled) {
			return;
		}
		
		Faction faction;
		
		if (parameters.size() > 0) {
			if (!Factions.hasPermViewAnyFactionBalance(sender)) {
				sendMessage("You do not have sufficient permissions to view the bank balance of other factions.");
				return;
			}
			faction = findFaction(parameters.get(0), true);
		} else {
			faction = me.getFaction();
		}
		
		if(faction == null) {
			sendMessage("Faction "+parameters.get(0)+" could not be found.");
			return;
		}
	
		sendMessage(Conf.colorChrome+faction.getTag()+" balance: "+ Econ.moneyString(faction.getMoney()));
	}
	
}
