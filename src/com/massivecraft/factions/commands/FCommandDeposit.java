package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Econ;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FCommandDeposit extends FBaseCommand {
	
	public FCommandDeposit() {
		aliases.add("deposit");
		
		helpDescription = "Deposit money into your faction's bank";
		requiredParameters.add("amount");
	}
	
	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if (!Conf.bankEnabled) {
			return;
		}
		
		double amount = 0.0;
		
		Faction faction = me.getFaction();
		
		if (parameters.size() == 1) {
			try {
				amount = Double.parseDouble(parameters.get(0));
			} catch (NumberFormatException e) {
				// wasn't valid
			}
		}
		
		String amountString = Econ.moneyString(amount);
		
		if( amount > 0.0 ) {
			if( !Econ.deductMoney(me.getName(), amount ) ) {
				sendMessage("You cannot afford to deposit that much.");
			}
			else
			{
				faction.addMoney(amount);
				sendMessage("You have deposited "+amountString+" into "+faction.getTag()+"'s bank.");
				sendMessage(faction.getTag()+" now has "+Econ.moneyString(faction.getMoney()));
				
				for (FPlayer fplayer : FPlayer.getAllOnline()) {
					if (fplayer.getFaction() == faction) {
						fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" has deposited "+amountString);
					}
				}
			}
		}
	}
	
}
