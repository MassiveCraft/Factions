package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandMod extends FBaseCommand {
	
	public FCommandMod() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("player name");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Give or revoke moderator rights";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! assertMinRole(Role.ADMIN)) {
			return;
		}
		
		String playerName = parameters.get(0);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if (you.getFaction() != myFaction) {
			sendMessage(you.getNameAndRelevant(me)+Conf.colorSystem+" is not a member in your faction.");
			return;
		}
		
		if (you == me) {
			sendMessage("The target player musn't be yourself.");
			return;
		}

		if (you.getRole() == Role.MODERATOR) {
			// Revoke
			you.setRole(Role.NORMAL);
			myFaction.sendMessage(you.getNameAndRelevant(myFaction)+Conf.colorSystem+" is no longer moderator in your faction.");
		} else {
			// Give
			you.setRole(Role.MODERATOR);
			myFaction.sendMessage(you.getNameAndRelevant(myFaction)+Conf.colorSystem+" was promoted to moderator in your faction.");
		}
	}
	
}
