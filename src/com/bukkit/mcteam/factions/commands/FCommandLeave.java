package com.bukkit.mcteam.factions.commands;

import org.bukkit.command.CommandSender;

public class FCommandLeave extends FBaseCommand {
	
	public FCommandLeave() {
		aliases.add("leave");
		
		helpDescription = "Leave your faction";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		me.leave();
	}
	
}
