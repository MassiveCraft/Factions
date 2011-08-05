package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FCommandPeaceful extends FBaseCommand {
	
	public FCommandPeaceful() {
		aliases.add("peaceful");
		
		senderMustBePlayer = false;
		
		requiredParameters.add("faction tag");
		
		helpDescription = "Designate a faction as peaceful";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermSetPeaceful(sender);
	}
	
	@Override
	public void perform() {
		if( parameters.size() >  0) {
			Faction faction = Faction.findByTag(parameters.get(0));
			
			if (faction == null) {
				sendMessage("No faction found with the tag \"" + parameters.get(0) + "\"");
				return;
			}

			if( faction != null && faction.isPeaceful() ) {
				sendMessage("Faction \"" + parameters.get(0) + "\" peaceful designation removed");
				faction.setPeaceful(false);
			} else {
				sendMessage("Faction \"" + faction.getTag() + "\" has been designated as peaceful");
				faction.setPeaceful(true);
			}
		}
	}
	
}
