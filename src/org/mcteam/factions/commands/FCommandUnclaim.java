package org.mcteam.factions.commands;

import org.mcteam.factions.Board;
import org.mcteam.factions.Conf;
import org.mcteam.factions.FLocation;
import org.mcteam.factions.Faction;
import org.mcteam.factions.Factions;
import org.mcteam.factions.struct.Role;

public class FCommandUnclaim extends FBaseCommand {
	
	public FCommandUnclaim() {
		aliases.add("unclaim");
		aliases.add("declaim");
		
		helpDescription = "Unclaim the land where you are standing";
	}
	
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		FLocation flocation = new FLocation(me);
		Faction otherFaction = Board.getFactionAt(flocation);
		
		if (otherFaction.isSafeZone()) {
			if (Factions.hasPermManageSafeZone(sender)) {
				Board.removeAt(flocation);
				sendMessage("Safe zone was unclaimed.");
			} else {
				sendMessage("This is a safe zone. You lack permissions to unclaim.");
			}
			return;
		}
		
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		
		if ( myFaction != otherFaction) {
			sendMessage("You don't own this land.");
			return;
		}
		
		Board.removeAt(flocation);
		
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" unclaimed some land.");
	}
	
}
