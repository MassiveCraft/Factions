package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandCreate extends FBaseCommand {
	
	public FCommandCreate() {
		aliases = new ArrayList<String>();
		aliases.add("create");
		
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("faction tag");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Create a new faction";
	}
	
	public void perform() {
		String tag = parameters.get(0);
		
		if (me.hasFaction()) {
			sendMessage("You must leave your current faction first.");
			return;
		}
		
		if (Faction.isTagTaken(tag)) {
			sendMessage("That tag is already in use.");
			return;
		}
		
		ArrayList<String> tagValidationErrors = Faction.validateTag(tag);
		if (tagValidationErrors.size() > 0) {
			sendMessage(tagValidationErrors);
			return;
		}
		
		Faction faction = Faction.create();
		faction.setTag(tag);
		me.setRole(Role.ADMIN);
		me.setFaction(faction);
		
		for (FPlayer follower : FPlayer.getAllOnline()) {
			follower.sendMessage(me.getNameAndRelevant(follower)+Conf.colorSystem+" created a new faction "+faction.getTag(follower));
		}
		
		sendMessage("You should now:");
		sendMessage( new FCommandDescription().getUseageTemplate() );
	}
	
}
