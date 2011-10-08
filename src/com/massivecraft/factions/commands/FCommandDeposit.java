package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;


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
		
		if( amount > 0.0 ) {
			String amountString = Econ.moneyString(amount);
			
			if( !Econ.deductMoney(me.getName(), amount ) ) {
				sendMessage("You cannot afford to deposit that much.");
			}
			else
			{
				faction.addMoney(amount);
				sendMessage("You have deposited "+amountString+" into "+faction.getTag()+"'s bank.");
				sendMessage(faction.getTag()+" now has "+Econ.moneyString(faction.getMoney()));
				P.log(player.getName() + " deposited "+amountString+" into "+faction.getTag()+"'s bank.");
				
				for (FPlayer fplayer : FPlayer.getAllOnline()) {
					if (fplayer.getFaction() == faction) {
						fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" has deposited "+amountString);
					}
				}
			}
		}
	}
	
}
