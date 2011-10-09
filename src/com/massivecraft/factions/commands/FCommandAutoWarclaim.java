package com.massivecraft.factions.commands;


import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class FCommandAutoWarclaim extends FCommand
{

	public FCommandAutoWarclaim()
	{
		super();
		this.aliases.add("autosafe");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flipp");
		
		this.permission = Permission.MANAGE_WAR_ZONE.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
		
		aliases.add("autowar");

		this.setHelpShort("Auto-claim land for the warzone");
	}

	@Override
	public void perform() {

		if ( isLocked() )
		{
			sendLockMessage();
			return;
		}

		boolean enabled = this.argAsBool(0, ! fme.isAutoWarClaimEnabled());

		fme.setIsAutoWarClaimEnabled(enabled);

		if ( ! enabled)
		{
			sendMessageParsed("<i>Auto-claiming of war zone disabled.");
			return;
		}

		sendMessageParsed("<i>Auto-claiming of war zone enabled.");

		FLocation playerFlocation = new FLocation(fme);
		
		if (!Board.getFactionAt(playerFlocation).isWarZone())
		{
			Board.setFactionAt(Factions.i.getWarZone(), playerFlocation);
			sendMessageParsed("<i>This land is now a war zone.");
		}
	}
	
}
