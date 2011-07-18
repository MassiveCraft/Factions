package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Factions;

public class FCommandSaveAll extends FBaseCommand {
	
	public FCommandSaveAll() {
		aliases.add("saveall");
		aliases.add("save");
		
		senderMustBePlayer = false;
		
		helpDescription = "save factions to disk";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermSaveAll(sender);
	}
	
	@Override
	public void perform() {
		Factions.saveAll();
		
		sendMessage("Factions saved to disk!");
	}
	
}
