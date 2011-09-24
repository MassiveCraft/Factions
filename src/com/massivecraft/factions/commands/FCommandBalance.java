package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Econ;
import com.massivecraft.factions.Faction;

public class FCommandBalance extends FBaseCommand {
	
	public FCommandBalance() {
		aliases.add("balance");
		aliases.add("money");
		
		helpDescription = "Shows the faction's current balance";
	}
	
	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if (!Conf.bankEnabled) {
			return;
		}
		
		Faction faction = me.getFaction();
		
		sendMessage(Conf.colorChrome+"Balance: "+ Econ.moneyString(faction.getMoney()));
	}
	
}
