package com.bukkit.mcteam.factions.commands;

import org.bukkit.command.CommandSender;

import com.bukkit.mcteam.factions.Board;
import com.bukkit.mcteam.factions.FLocation;
import com.bukkit.mcteam.factions.Faction;
import com.bukkit.mcteam.factions.Factions;

public class FCommandSafeclaim extends FBaseCommand {
	
	public FCommandSafeclaim() {
		aliases.add("safeclaim");
		aliases.add("safe");
		
		helpDescription = "Claim land for the safezone";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermManageSafeZone(sender);
	}
	
	public void perform() {
		FLocation flocation = new FLocation(me);
		Board.setFactionAt(Faction.getSafeZone(), flocation);
		sendMessage("This land is now a safe zone");
	}
	
}
