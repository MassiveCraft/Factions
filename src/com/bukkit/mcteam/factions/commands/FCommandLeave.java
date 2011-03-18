package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;

public class FCommandLeave extends FCommand {
	
	public FCommandLeave() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpNameAndParams = "leave";
		helpDescription = "Leave your faction";
	}
	
	public void perform() {
		Faction faction = fplayer.getFaction();
		
		ArrayList<String> errors = fplayer.leave();
		fplayer.sendMessage(errors);
		
		if (errors.size() == 0) {
			faction.sendMessage(fplayer.getNameAndRelevant(faction)+Conf.colorSystem+" left your faction.");
			fplayer.sendMessage("You left "+faction.getTag(fplayer));
		}
		
		if (faction.getFollowersAll().size() == 0) {
			// Remove this faction
			for (FPlayer follower : FPlayer.getAll()) {
				follower.sendMessage(Conf.colorSystem+"The faction "+faction.getTag(follower)+Conf.colorSystem+" was disbanded.");
			}
			Faction.delete(faction.id);
		}
	}
	
}
