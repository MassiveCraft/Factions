package org.mcteam.factions.commands;

import org.bukkit.command.CommandSender;
import org.mcteam.factions.Faction;
import org.mcteam.factions.Factions;

public class FCommandDisband extends FBaseCommand {
	
	public FCommandDisband() {
		aliases.add("disband");
		
		senderMustBePlayer = false;
		
		requiredParameters.add("faction tag");
		
		helpDescription = "Disband a faction";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermDisband(sender);
	}
	
	@Override
	public void perform() {
		if( parameters.size() >  0) {
			Faction faction = Faction.findByTag(parameters.get(0));
			
			if( faction != null && faction.getId() > 0 ) {
				sendMessage("Faction " + faction.getTag() + " got disbanded");
				Faction.delete( faction.getId() );
			} else {
				sendMessage("Faction " + parameters.get(0) + "not found");
			}
		}
	}
	
}
