package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

public class FCommandUnclaimall extends FCommand {
	
	public FCommandUnclaimall() {
		aliases.add("unclaimall");
		aliases.add("declaimall");
		
		helpDescription = "Unclaim all of your factions land";
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

		Faction myFaction = me.getFaction();

		String moneyBack = "";
		if (Econ.enabled()) {
			double refund = Econ.calculateTotalLandRefund(myFaction.getLandRounded());
			// a real refund
			if (refund > 0.0) {
				if(Conf.bankFactionPaysLandCosts) {
					Faction faction = me.getFaction();
					faction.addMoney(refund);
					moneyBack = " "+faction.getTag()+" received a refund of "+Econ.moneyString(refund)+".";
				} else {
					Econ.addMoney(me.getName(), refund);
					moneyBack = " They received a refund of "+Econ.moneyString(refund)+".";
				}
			}
			// wait, you're charging people to unclaim land? outrageous
			else if (refund < 0.0) {
				if(Conf.bankFactionPaysLandCosts) {
					Faction faction = me.getFaction();
					if(!faction.removeMoney(-refund)) {
						sendMessage("Unclaiming all faction land will cost "+Econ.moneyString(-refund)+", which your faction can't currently afford.");
						return;
					}
					moneyBack = " It cost "+faction.getTag()+" "+Econ.moneyString(refund)+".";
				} else {
					if (!Econ.deductMoney(me.getName(), -refund)) {
						sendMessage("Unclaiming all faction land will cost "+Econ.moneyString(-refund)+", which you can't currently afford.");
						return;
					}
					moneyBack = " It cost them "+Econ.moneyString(refund)+".";
				}
				moneyBack = " It cost them "+Econ.moneyString(refund)+".";
			}
			// no refund
			else {
				moneyBack = "";
			}
		}

		Board.unclaimAll(myFaction.getId());
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" unclaimed ALL of your faction's land."+moneyBack);
	}
	
}
