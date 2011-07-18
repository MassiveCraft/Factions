package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FCommandAutoSafeclaim extends FBaseCommand {

	public FCommandAutoSafeclaim() {
		aliases.add("autosafe");

		optionalParameters.add("on|off");

		helpDescription = "Auto-claim land for the safezone";
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

		boolean enable = !me.autoSafeZoneEnabled();

		if (parameters.size() > 0)
			enable = parseBool(parameters.get(0));

		me.enableAutoSafeZone(enable);

		if (!enable) {
			sendMessage("Auto-claiming of safe zone disabled.");
			return;
		}

		sendMessage("Auto-claiming of safe zone enabled.");

		FLocation playerFlocation = new FLocation(me);
		
		if (!Board.getFactionAt(playerFlocation).isSafeZone()) {
			Board.setFactionAt(Faction.getSafeZone(), playerFlocation);
			sendMessage("This land is now a safe zone.");
		}
	}
	
}
