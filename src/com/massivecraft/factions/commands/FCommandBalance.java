package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Econ;
import com.massivecraft.factions.Faction;

public class FCommandBalance extends FBaseCommand {
	
	public FCommandBalance() {
		aliases.add("balance");
		aliases.add("money");
		
		optionalParameters.add("faction name");
		
		helpDescription = "Shows a faction's current balance";
	}
	
	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if (!Conf.bankEnabled) {
			return;
		}
		
		String factionName = parameters.get(0);
		Faction faction = findFaction(factionName, true);
		
		sendMessage(Conf.colorChrome+faction.getTag()+"'s balance: "+ Econ.moneyString(faction.getMoney()));
	}
	
}
