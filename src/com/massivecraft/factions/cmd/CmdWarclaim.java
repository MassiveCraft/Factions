package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class CmdWarclaim extends FCommand
{
	
	public CmdWarclaim()
	{
		this.aliases.add("warclaim");
		this.aliases.add("war");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("radius", "0");
		
		this.permission = Permission.MANAGE_WAR_ZONE.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		
		this.setHelpShort("Claim land for the warzone");
	}
	
	public void perform()
	{
		// The current location of the player
		FLocation playerFlocation = new FLocation(fme);
		
		int radius = this.argAsInt(0, 0);
		if (radius < 0) radius = 0;
		
		FLocation from = playerFlocation.getRelative(radius, radius);
		FLocation to = playerFlocation.getRelative(-radius, -radius);
		
		for (FLocation locToClaim : FLocation.getArea(from, to))
		{
			Board.setFactionAt(Factions.i.getWarZone(), locToClaim);
		}
		
		msg("<i>You claimed <h>%d chunks<i> for the <a>war zone<i>.", (1+radius*2)*(1+radius*2));
	}
}
