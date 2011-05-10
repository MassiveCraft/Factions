package org.mcteam.factions.commands;

import org.bukkit.command.CommandSender;
import org.mcteam.factions.Board;
import org.mcteam.factions.Conf;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.Faction;
import org.mcteam.factions.Factions;

public class FCommandReload extends FBaseCommand {
	
	public FCommandReload() {
		aliases.add("reload");
		
		helpDescription = "reloads the config";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermReload(sender);
	}
	
	public void perform() {
		Factions.log("=== RELOAD START ===");
		long timeInitStart = System.currentTimeMillis();
		
		Conf.load();
		FPlayer.load();
		Faction.load();
		Board.load();
		
		long timeReload = (System.currentTimeMillis()-timeInitStart);
		Factions.log("=== RELOAD DONE (Took "+timeReload+"ms) ===");
		
		me.sendMessage("FACTIONS RELOAD DONE IN " + timeReload + "ms");
	}
	
}
