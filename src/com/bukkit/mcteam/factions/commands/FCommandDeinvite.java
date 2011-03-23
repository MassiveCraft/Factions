package com.bukkit.mcteam.factions.commands;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.FPlayer;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandDeinvite extends FBaseCommand {
	
	public FCommandDeinvite() {
		aliases.add("deinvite");
		aliases.add("deinv");
		
		requiredParameters.add("player name");
		
		helpDescription = "Remove a pending invitation";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		String playerName = parameters.get(0);
		
		FPlayer you = findFPlayer(playerName, false);
		if (you == null) {
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if ( ! assertMinRole(Role.MODERATOR)) {
			return;
		}
		
		if (you.getFaction() == myFaction) {
			sendMessage(you.getName()+" is already a member of "+myFaction.getTag());
			sendMessage("You might want to: " + new FCommandKick().getUseageTemplate());
			return;
		}
		
		myFaction.deinvite(you);
		
		you.sendMessage(me.getNameAndRelevant(you)+Conf.colorSystem+" revoked your invitation to "+myFaction.getTag(you));
		myFaction.sendMessage(me.getNameAndRelevant(me)+Conf.colorSystem+" revoked "+you.getNameAndRelevant(me)+"'s"+Conf.colorSystem+" invitation.");
	}
	
}
