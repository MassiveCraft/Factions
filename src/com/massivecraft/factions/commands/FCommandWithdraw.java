package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Role;


public class FCommandWithdraw extends FBaseCommand {
	
	public FCommandWithdraw() {
		aliases.add("withdraw");
		
		helpDescription = "Withdraw money from your faction's bank";
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
		
		if ( !Conf.bankMembersCanWithdraw && !assertMinRole(Role.MODERATOR)) {
			sendMessage("Only faction moderators or admins are able to withdraw from the bank.");
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

			if( amount > faction.getMoney() ) {
				amount = faction.getMoney();
			}
			
			faction.removeMoney(amount);
			Econ.addMoney(me.getName(), amount);
			sendMessage("You have withdrawn "+amountString+" from "+faction.getTag()+"'s bank.");
			sendMessage(faction.getTag()+" now has "+Econ.moneyString(faction.getMoney()));
			Factions.log(player.getName() + " withdrew "+amountString+" from "+faction.getTag()+"'s bank.");
			
			for (FPlayer fplayer : FPlayer.getAllOnline()) {
				if (fplayer.getFaction() == faction) {
					fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" has withdrawn "+amountString);
				}
			}
		}
	}
	
}
