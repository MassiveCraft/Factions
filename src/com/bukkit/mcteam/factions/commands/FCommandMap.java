package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Board;
import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FLocation;

public class FCommandMap extends FBaseCommand {
	
	public FCommandMap() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		optionalParameters.add("on|off");
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Show territory map, set optional auto update";
	}
	
	public void perform() {
		if (parameters.size() > 0) {
			String mapAutoUpdating = parameters.get(0);
			if (Conf.aliasTrue.contains(mapAutoUpdating.toLowerCase())) {
				// Turn on
				me.setMapAutoUpdating(true);
				sendMessage("Map auto update ENABLED.");
				
				// And show the map once
				showMap();
			} else {
				// Turn off
				me.setMapAutoUpdating(false);
				sendMessage("Map auto update DISABLED.");
			}
		} else {
			showMap();
		}
	}
	
	public void showMap() {
		sendMessage(Board.getMap(me.getFaction(), new FLocation(me), me.getPlayer().getLocation().getYaw()));
	}
	
}
