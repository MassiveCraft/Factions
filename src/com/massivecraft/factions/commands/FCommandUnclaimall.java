package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.struct.Role;

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
