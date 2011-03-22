package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.struct.Role;
import com.bukkit.mcteam.factions.util.TextUtil;

public class FCommandDescription extends FBaseCommand {
	
	public FCommandDescription() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		requiredParameters.add("description");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Change the faction description";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		me.getFaction().setDescription(TextUtil.implode(parameters));
		// Broadcast the description to everyone
		for (FPlayer fplayer : FPlayer.getAllOnline()) {
			fplayer.sendMessage("The faction "+fplayer.getRelationColor(me)+me.getFaction().getTag()+Conf.colorSystem+" changed their description to:");
			fplayer.sendMessage(me.getFaction().getDescription());
		}
	}
	
}
