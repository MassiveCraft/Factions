package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Board;
import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FLocation;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandUnclaim extends FBaseCommand {
	
	public FCommandUnclaim() {
		aliases = new ArrayList<String>();
		aliases.add("unclaim");
		aliases.add("declaim");
		
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Unclaim the land where you are standing";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		FLocation flocation = new FLocation(me);
		Faction otherFaction = Board.getFactionAt(flocation);
		
		if ( myFaction != otherFaction) {
			sendMessage("You don't own this land.");
			return;
		}
		
		Board.removeAt(flocation);
		
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" unclaimed some land.");
	}
	
}
