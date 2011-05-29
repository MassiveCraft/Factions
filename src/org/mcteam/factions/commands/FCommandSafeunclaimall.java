package org.mcteam.factions.commands;

import org.bukkit.command.CommandSender;
import org.mcteam.factions.Board;
import org.mcteam.factions.Faction;
import org.mcteam.factions.Factions;

public class FCommandSafeunclaimall extends FBaseCommand {
	
	public FCommandSafeunclaimall() {
		aliases.add("safeunclaimall");
		aliases.add("safedeclaimall");
		
		helpDescription = "Unclaim all safezone land";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermManageSafeZone(sender);
	}
	
	@Override
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		Board.unclaimAll(Faction.getSafeZone().getId());
		sendMessage("You unclaimed ALL safe zone land.");
	}
	
}
