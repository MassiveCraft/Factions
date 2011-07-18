package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;


public class FCommandMap extends FBaseCommand {
	
	public FCommandMap() {
		aliases.add("map");
		
		optionalParameters.add("on|off");
		
		helpDescription = "Show territory map, set optional auto update";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}
	
	@Override
	public void perform() {
		if (parameters.size() > 0) {
			String mapAutoUpdating = parameters.get(0);
			if (parseBool(mapAutoUpdating)) {
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
