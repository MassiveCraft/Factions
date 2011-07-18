package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Factions;

public class FCommandLock extends FBaseCommand {
	
	public FCommandLock() {
		aliases.add("lock");
		
		senderMustBePlayer = false;
		
		optionalParameters.add("on|off");
		
		helpDescription = "lock all write stuff";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermLock(sender);
	}
	
	@Override
	public void perform() {
		if( parameters.size() > 0 ) {
			setLock( parseBool( parameters.get(0) ));
		} else {
			if( isLocked() ) {
				sendMessage("Factions is locked");
			} else {
				sendMessage("Factions is not locked");
			}
		}
	}
	
}
