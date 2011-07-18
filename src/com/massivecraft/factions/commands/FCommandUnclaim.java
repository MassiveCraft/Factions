package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Role;

public class FCommandUnclaim extends FBaseCommand {
	
	public FCommandUnclaim() {
		aliases.add("unclaim");
		aliases.add("declaim");
		
		helpDescription = "Unclaim the land where you are standing";
	}
	
	@Override
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
		else if (otherFaction.isWarZone()) {
			if (Factions.hasPermManageWarZone(sender)) {
				Board.removeAt(flocation);
				sendMessage("War zone was unclaimed.");
			} else {
				sendMessage("This is a war zone. You lack permissions to unclaim.");
			}
			return;
		}
		
		if (Conf.adminBypassPlayers.contains(player.getName())) {
			Board.removeAt(flocation);

			otherFaction.sendMessage(me.getNameAndRelevant(otherFaction)+Conf.colorSystem+" unclaimed some of your land.");
			sendMessage("You unclaimed this land.");
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
