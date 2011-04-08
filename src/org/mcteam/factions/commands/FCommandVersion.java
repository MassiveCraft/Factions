package org.mcteam.factions.commands;

import org.bukkit.command.CommandSender;
import org.mcteam.factions.Factions;


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
	
	
	public void perform() {
		sendMessage("You are running "+Factions.instance.getDescription().getFullName());
	}
	
}
