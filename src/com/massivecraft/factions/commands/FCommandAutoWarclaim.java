package com.massivecraft.factions.commands;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FCommandAutoWarclaim extends FBaseCommand {

	public FCommandAutoWarclaim() {
		aliases.add("autowar");

		optionalParameters.add("on|off");

		helpDescription = "Auto-claim land for the warzone";
	}

	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermManageWarZone(sender);
	}

	@Override
	public void perform() {

		if( isLocked() ) {
			sendLockMessage();
			return;
		}

		boolean enable = !me.autoWarZoneEnabled();

		if (parameters.size() > 0)
			enable = parseBool(parameters.get(0));

		me.enableAutoWarZone(enable);

		if (!enable) {
			sendMessage("Auto-claiming of war zone disabled.");
			return;
		}

		sendMessage("Auto-claiming of war zone enabled.");

		FLocation playerFlocation = new FLocation(me);
		
		if (!Board.getFactionAt(playerFlocation).isWarZone()) {
			Board.setFactionAt(Faction.getWarZone(), playerFlocation);
			sendMessage("This land is now a war zone.");
		}
	}
	
}
