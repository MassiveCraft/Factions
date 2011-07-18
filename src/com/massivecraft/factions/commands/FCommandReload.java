package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FCommandReload extends FBaseCommand {
	
	public FCommandReload() {
		aliases.add("reload");
		
		senderMustBePlayer = false;
		
		optionalParameters.add("file");
		
		helpDescription = "reloads all json files, or a specific one";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermReload(sender);
	}
	
	@Override
	public void perform() {
		Factions.log("=== RELOAD START ===");
		long timeInitStart = System.currentTimeMillis();
		String fileName = "s";
		
		// Was a single file specified?
		if (parameters.size() > 0) {
			String file = parameters.get(0);
			if (file.equalsIgnoreCase("conf") || file.equalsIgnoreCase("conf.json")) {
				Factions.log("RELOADING CONF.JSON");
				Conf.load();
				fileName = " conf.json";
			}
			else if (file.equalsIgnoreCase("board") || file.equalsIgnoreCase("board.json")) {
				Factions.log("RELOADING BOARD.JSON");
				Board.load();
				fileName = " board.json";
			}
			else if (file.equalsIgnoreCase("factions") || file.equalsIgnoreCase("factions.json")) {
				Factions.log("RELOADING FACTIONS.JSON");
				Faction.load();
				fileName = " factions.json";
			}
			else if (file.equalsIgnoreCase("players") || file.equalsIgnoreCase("players.json")) {
				Factions.log("RELOADING PLAYERS.JSON");
				FPlayer.load();
				fileName = " players.json";
			}
			else {
				Factions.log("RELOAD CANCELLED - SPECIFIED FILE INVALID");
				sendMessage("Invalid file specified. Valid files: conf, board, factions, players.");
				return;
			}
		}
		else {
			Factions.log("RELOADING ALL FILES");
			Conf.load();
			FPlayer.load();
			Faction.load();
			Board.load();
		}
		
		long timeReload = (System.currentTimeMillis()-timeInitStart);
		Factions.log("=== RELOAD DONE (Took "+timeReload+"ms) ===");
		
		sendMessage("Factions file" + fileName + " reloaded from disk, took " + timeReload + "ms");
	}
	
}
