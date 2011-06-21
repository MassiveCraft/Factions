package org.mcteam.factions.commands;

import org.mcteam.factions.Board;
import org.mcteam.factions.Conf;
import org.mcteam.factions.Faction;
import org.mcteam.factions.struct.Role;

public class FCommandUnclaimall extends FBaseCommand {
	
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
		
		Board.unclaimAll(myFaction.getId());
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" unclaimed ALL of your factions land.");
	}
	
}
