package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;

public class FCommandKick extends FBaseCommand {
	
	public FCommandKick() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("player name");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Kick a player from the faction";
	}
	
	public void perform() {
		String playerName = parameters.get(0);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		Faction myFaction = me.getFaction();

		if (you.getFaction() != myFaction) {
			sendMessage(you.getNameAndRelevant(me)+Conf.colorSystem+" is not a member of "+myFaction.getTag(me));
			return;
		}
		
		if (me == you) {
			sendMessage("You cannot kick yourself.");
			sendMessage("You might want to "+Conf.colorCommand+Conf.aliasBase.get(0)+" "+Conf.aliasLeave.get(0));
			return;
		}
		
		if (you.role.value >= me.role.value) { // TODO add more informative messages.
			sendMessage("Your rank is too low to kick this player.");
			return;
		}
		
		myFaction.invites.remove(you.playerName);
		you.resetFactionData();
		FPlayer.save();
		Faction.save();	
		
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" kicked "+you.getNameAndRelevant(myFaction)+Conf.colorSystem+" from the faction! :O");
		you.sendMessage(me.getNameAndRelevant(you)+Conf.colorSystem+" kicked you from "+myFaction.getTag(you)+Conf.colorSystem+"! :O");
	}
	
}
