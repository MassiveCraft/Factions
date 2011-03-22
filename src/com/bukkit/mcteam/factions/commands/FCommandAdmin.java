package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandAdmin extends FBaseCommand {
	
	public FCommandAdmin() {
		aliases = new ArrayList<String>();
		aliases.add("admin");
		
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("player name");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Hand over your admin rights";
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

		
		me.setRole(Role.MODERATOR);
		you.setRole(Role.ADMIN);
		
		// Inform all players
		for (FPlayer fplayer : FPlayer.getAllOnline()) {
			if (fplayer.getFaction() == me.getFaction()) {
				fplayer.sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" gave "+you.getNameAndRelevant(me)+Conf.colorSystem+" the leadership of your faction.");
			} else {
				fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" gave "+you.getNameAndRelevant(fplayer)+Conf.colorSystem+" the leadership of "+myFaction.getTag(fplayer));
			}
		}
	}
	
}
