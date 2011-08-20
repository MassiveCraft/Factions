package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SpoutFeatures;

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
			
			if( faction == null || !faction.isNormal()) {
				sendMessage("Faction " + parameters.get(0) + "not found");
				return;
			}

			// Inform all players
			for (FPlayer fplayer : FPlayer.getAllOnline()) {
				if (fplayer.getFaction() == faction) {
					fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" disbanded your faction.");
				} else {
					fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" disbanded the faction "+faction.getTag(fplayer)+".");
				}
			}
			Faction.delete( faction.getId() );
			SpoutFeatures.updateAppearances();
		}
	}
	
}
