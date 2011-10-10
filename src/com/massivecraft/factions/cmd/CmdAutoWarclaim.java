package com.massivecraft.factions.cmd;


import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class CmdAutoWarclaim extends FCommand
{

	public CmdAutoWarclaim()
	{
		super();
		this.aliases.add("autosafe");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flipp");
		
		this.permission = Permission.MANAGE_WAR_ZONE.node;
		this.disableOnLock = true;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
		
		aliases.add("autowar");

		this.setHelpShort("Auto-claim land for the warzone");
	}

	@Override
	public void perform()
	{
		boolean enabled = this.argAsBool(0, ! fme.isAutoWarClaimEnabled());

		fme.setIsAutoWarClaimEnabled(enabled);

		if ( ! enabled)
		{
			msg("<i>Auto-claiming of war zone disabled.");
			return;
		}

		msg("<i>Auto-claiming of war zone enabled.");

		FLocation playerFlocation = new FLocation(fme);
		
		if (!Board.getFactionAt(playerFlocation).isWarZone())
		{
			Board.setFactionAt(Factions.i.getWarZone(), playerFlocation);
			msg("<i>This land is now a war zone.");
		}
	}
	
}
