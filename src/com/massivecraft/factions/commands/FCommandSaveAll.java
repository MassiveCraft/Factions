package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.P;

public class FCommandSaveAll extends FCommand {
	
	public FCommandSaveAll() {
		aliases.add("saveall");
		aliases.add("save");
		
		senderMustBePlayer = false;
		
		helpDescription = "save factions to disk";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return P.hasPermSaveAll(sender);
	}
	
	@Override
	public void perform() {
		P.saveAll();
		
		sendMessage("Factions saved to disk!");
	}
	
}
