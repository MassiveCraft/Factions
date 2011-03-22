package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

public class FCommandLeave extends FBaseCommand {
	
	public FCommandLeave() {
		aliases = new ArrayList<String>();
		aliases.add("leave");
		
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Leave your faction";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		me.leave();
	}
	
}
