package com.bukkit.mcteam.factions.commands;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.util.TextUtil;

public class FCommandTitle extends FBaseCommand {
	
	public FCommandTitle() {
		aliases.add("title");
		
		requiredParameters.add("player name");
		
		optionalParameters.add("title");
		
		helpDescription = "Set or remove a players title";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		String playerName = parameters.get(0);
		parameters.remove(0);
		String title = TextUtil.implode(parameters);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		if ( ! canIAdministerYou(me, you)) {
			return;
		}
		
		you.setTitle(title);
		
		// Inform
		Faction myFaction = me.getFaction();
		myFaction.sendMessage(me.getNameAndRelevant(myFaction)+Conf.colorSystem+" changed a title: "+you.getNameAndRelevant(myFaction));
	}
	
}
