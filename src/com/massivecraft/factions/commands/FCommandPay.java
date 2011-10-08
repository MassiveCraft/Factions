package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.struct.Role;


public class FCommandPay extends FBaseCommand {
	
	public FCommandPay() {
		aliases.add("pay");
		
		helpDescription = "Pay another faction from your bank";
		requiredParameters.add("faction");
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
			sendMessage("Only faction moderators or admins are able to pay another faction.");
			return;
		}
		
		double amount = 0.0;
		
		Faction us = me.getFaction();
		Faction them = null;
		
		if (parameters.size() == 2) {
			try {
				them = Faction.findByTag(parameters.get(0));
				amount = Double.parseDouble(parameters.get(1));
			} catch (NumberFormatException e) {
				// wasn't valid
			}
		}
		
		if(them == null) {
			sendMessage("Faction "+parameters.get(0)+" could not be found.");
			return;
		}

		if( amount > 0.0 ) {
			String amountString = Econ.moneyString(amount);

			if( amount > us.getMoney() ) {
				amount = us.getMoney();
			}
			
			us.removeMoney(amount);
			them.addMoney(amount);
			sendMessage("You have paid "+amountString+" from "+us.getTag()+"'s bank to "+them.getTag()+"'s bank.");
			sendMessage(us.getTag()+" now has "+Econ.moneyString(us.getMoney()));
			P.log(player.getName() + " paid "+amountString+" from "+us.getTag()+"'s bank to "+them.getTag()+"'s bank.");
			
			for (FPlayer fplayer : FPlayer.getAllOnline()) {
				if (fplayer.getFaction() == us || fplayer.getFaction() == them) {
					fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" has sent "+amountString+" from "+us.getTag()+" to "+them.getTag() );
				}
			}
		}
	}
	
}
