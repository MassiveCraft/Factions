package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Factions;


public class FCommandVersion extends FBaseCommand {
	
	public FCommandVersion() {
		aliases.add("version");
		
		senderMustBePlayer = false;
		
		helpDescription = "Which version are you using?";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return true;
	}
	
	@Override
	public void perform() {
		sendMessage("You are running "+Factions.instance.getDescription().getFullName());
	}
	
}
