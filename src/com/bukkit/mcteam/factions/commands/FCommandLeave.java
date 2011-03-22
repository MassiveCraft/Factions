package com.bukkit.mcteam.factions.commands;

import java.util.ArrayList;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandLeave extends FBaseCommand {
	
	public FCommandLeave() {
		requiredParameters = new ArrayList<String>();
		optionalParameters = new ArrayList<String>();
		
		permissions = "";
		
		senderMustBePlayer = true;
		
		helpDescription = "Leave your faction";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		Faction faction = me.getFaction();
		
		if (me.role == Role.ADMIN && faction.getFPlayers().size() > 1) {
			sendMessage("You must give the admin role to someone else first.");
			return;
		}
		
		faction.sendMessage(me.getNameAndRelevant(faction) + Conf.colorSystem + " left your faction.");
		me.resetFactionData();
		FPlayer.save();
		
		if (faction.getFPlayers().size() == 0) {
			// Remove this faction
			for (FPlayer fplayer : FPlayer.getAllOnline()) {
				fplayer.sendMessage("The faction "+faction.getTag(fplayer)+Conf.colorSystem+" was disbanded.");
			}
			Faction.delete(faction.id);
		}
	}
	
}
