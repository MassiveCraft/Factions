package org.mcteam.factions.commands;

import org.bukkit.command.CommandSender;
import org.mcteam.factions.Board;
import org.mcteam.factions.FLocation;
import org.mcteam.factions.Faction;
import org.mcteam.factions.Factions;


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
