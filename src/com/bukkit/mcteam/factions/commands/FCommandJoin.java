package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;

public class FCommandJoin extends FBaseCommand {
	
	public FCommandJoin() {
		aliases = new ArrayList<String>();
		aliases.add("join");
		
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("faction name");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Join a faction";
	}
	
	public void perform() {
		String factionName = parameters.get(0);
		
		Faction faction = findFaction(factionName);
		if (faction == null) {
			return;
		}

		if (faction == me.getFaction()) {
			sendMessage("You are already a member of "+faction.getTag(me));
			return;
		}
		
		if (me.hasFaction()) {
			sendMessage("You must leave your current faction first.");
			return;
		}
		
		if( ! faction.getOpen() && ! faction.isInvited(me)) {
			sendMessage("This guild requires invitation.");
			faction.sendMessage(me.getNameAndRelevant(faction)+Conf.colorSystem+" tried to join your faction.");
			return;
		}

		me.sendMessage(Conf.colorSystem+"You successfully joined "+faction.getTag(me));
		faction.sendMessage(me.getNameAndRelevant(faction)+Conf.colorSystem+" joined your faction.");
		
		me.resetFactionData();
		me.setFaction(faction);
		faction.deinvite(me);
		FPlayer.save();
	}
	
}
