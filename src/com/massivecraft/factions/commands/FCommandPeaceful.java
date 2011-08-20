package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SpoutFeatures;

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

			String change;
			if(faction.isPeaceful()) {
				change = "removed peaceful status from";
				faction.setPeaceful(false);
			} else {
				change = "granted peaceful status to";
				faction.setPeaceful(true);
			}
			// Inform all players
			for (FPlayer fplayer : FPlayer.getAllOnline()) {
				if (fplayer.getFaction() == faction) {
					fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" has "+change+" your faction.");
				} else {
					fplayer.sendMessage(me.getNameAndRelevant(fplayer)+Conf.colorSystem+" has "+change+" the faction \"" + faction.getTag(fplayer) + "\".");
				}
			}

			SpoutFeatures.updateAppearances(faction);
		}
	}
	
}
