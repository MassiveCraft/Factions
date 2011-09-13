package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.SpoutFeatures;
import com.massivecraft.factions.struct.Role;


public class FCommandDisband extends FBaseCommand {
	
	public FCommandDisband() {
		aliases.add("disband");
		
		senderMustBePlayer = false;
		
		optionalParameters.add("faction tag");
		
		helpDescription = "Disband a faction";
	}
	
	@Override
	public void perform() {

		Faction faction = null;

		if( parameters.size() > 0) {
			faction = Faction.findByTag(parameters.get(0));
			
			if( faction == null || !faction.isNormal()) {
				sendMessage("Faction \"" + parameters.get(0) + "\" not found");
				return;
			}

			if ( ! Factions.hasPermDisband(sender)) {
				if (me.getFaction() == faction) {
					parameters.clear();
				}
				else {
					sendMessage("You do not have sufficient permission to disband other factions.");
					return;
				}
			}
		}
		if (parameters.isEmpty()) {
			if ( ! assertHasFaction()) {
				return;
			}
			if ( ! assertMinRole(Role.ADMIN)) {
				return;
			}

			faction = me.getFaction();

			if (faction.isPermanent() && !Factions.hasPermDisband(sender)) {
				sendMessage("Your faction is designated as permanent, so you cannot disband it.");
				return;
			}
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
