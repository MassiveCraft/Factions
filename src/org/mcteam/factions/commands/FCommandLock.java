package org.mcteam.factions.commands;

import org.bukkit.command.CommandSender;
import org.mcteam.factions.Factions;

public class FCommandLock extends FBaseCommand {
	
	public FCommandLock() {
		aliases.add("lock");
		
		optionalParameters.add("on|off");
		
		helpDescription = "lock all write stuff";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermLock(sender);
	}
	
	public void perform() {
		if( parameters.size() > 0 ) {
			setLock( parseBool( parameters.get(0) ));
		} else {
			if( isLocked() ) {
				me.sendMessage("Factions is locked");
			} else {
				me.sendMessage("Factions is not locked");
			}
		}
	}
	
}
