package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Factions;

public class FCommandVersion extends FBaseCommand {
	
	public FCommandVersion() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = false;
		
		helpDescription = "Which version are you using?";
	}
	
	public void perform() {
		sendMessage("You are running "+Factions.instance.getDescription().getFullName());
	}
	
}
