package com.bukkit.mcteam.factions.commands;

import com.bukkit.mcteam.factions.Conf;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.struct.Role;

public class FCommandHome extends FBaseCommand {
	
	public FCommandHome() {
		aliases.add("home");
		
		helpDescription = "Teleport to the faction home";
	}
	
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! Conf.homesEnabled) {
			me.sendMessage("Sorry, Faction homes are disabled on this server.");
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if ( ! myFaction.hasHome()) {
			me.sendMessage("You faction does not have a home. " + (me.getRole().value < Role.MODERATOR.value ? " Ask your leader to:" : "You should:"));
			me.sendMessage(new FCommandSethome().getUseageTemplate(true, true));
			return;
		}
		
		player.teleportTo(myFaction.getHome());
	}
	
}
