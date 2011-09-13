package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FPlayer;


public class FCommandPermanent extends FBaseCommand {
	
	public FCommandPermanent() {
		aliases.add("permanent");
		
		senderMustBePlayer = false;
		
		requiredParameters.add("faction tag");
		
		helpDescription = "Designate a faction as permanent";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermSetPermanent(sender);
	}
	
	@Override
	public void perform() {
		if( parameters.size() >  0) {
			Faction faction = Faction.findByTag(parameters.get(0));
			
			if (faction == null) {
				sendMessage("No faction found with the tag \"" + parameters.get(0) + "\"");
				return;
			}

			String change;
			if(faction.isPermanent()) {
				change = "removed permanent status from";
				faction.setPermanent(false);
			} else {
				change = "added permanent status to";
				faction.setPermanent(true);
			}
			// Inform all players
			for (FPlayer fplayer : FPlayer.getAllOnline()) {
				if (fplayer.getFaction() == faction) {
					fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" has "+change+" your faction.");
				} else {
					fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" has "+change+" the faction \"" + faction.getTag(fplayer) + "\".");
				}
			}
		}
	}
	
}
