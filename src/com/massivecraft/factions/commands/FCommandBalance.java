package com.massivecraft.factions.commands;

import org.bukkit.entity.Player;

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
		
		Faction faction;
		
		if (parameters.size() > 0) {
			faction = findFaction(parameters.get(0), true);
		} else {
			faction = me.getFaction();
		}
		
		sendMessage(Conf.colorChrome+faction.getTag()+" balance: "+ Econ.moneyString(faction.getMoney()));
	}
	
}
