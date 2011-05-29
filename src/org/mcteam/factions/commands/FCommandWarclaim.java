package org.mcteam.factions.commands;

import org.bukkit.command.CommandSender;
import org.mcteam.factions.Board;
import org.mcteam.factions.FLocation;
import org.mcteam.factions.Faction;
import org.mcteam.factions.Factions;

public class FCommandWarclaim extends FBaseCommand {
	
	public FCommandWarclaim() {
		aliases.add("warclaim");
		aliases.add("war");
		
		optionalParameters.add("radius");
		
		helpDescription = "Claim land for the warzone";
	}
	
	@Override
	public boolean hasPermission(CommandSender sender) {
		return Factions.hasPermManageWarZone(sender);
	}
	
	public void perform() {
		
		if( isLocked() ) {
			sendLockMessage();
			return;
		}
		
		// The current location of the player
		FLocation playerFlocation = new FLocation(me);
		
		// Was a radius set?
		if (parameters.size() > 0) {
			int radius = Integer.parseInt(parameters.get(0));
			
			FLocation from = playerFlocation.getRelative(radius, radius);
			FLocation to = playerFlocation.getRelative(-radius, -radius);
			
			for (FLocation locToClaim : FLocation.getArea(from, to)) {
				Board.setFactionAt(Faction.getWarZone(), locToClaim);
			}
			
			sendMessage("You claimed "+(1+radius*2)*(1+radius*2)+" chunks for the war zone.");
			
		} else {
			Board.setFactionAt(Faction.getWarZone(), playerFlocation);
			sendMessage("This land is now a war zone");
		}
	}
	
}
