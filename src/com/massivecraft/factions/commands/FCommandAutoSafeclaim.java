package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class FCommandAutoSafeclaim extends FCommand
{

	public FCommandAutoSafeclaim()
	{
		super();
		this.aliases.add("autosafe");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flipp");
		
		this.permission = Permission.MANAGE_SAFE_ZONE.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = true;
		senderMustBeAdmin = false;
		
		this.setHelpShort("Auto-claim land for the safezone");
	}

	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}

		boolean enabled = this.argAsBool(0, ! fme.isAutoSafeClaimEnabled());
		
		fme.setIsAutoSafeClaimEnabled(enabled);

		if ( ! enabled)
		{
			sendMessageParsed("<i>Auto-claiming of safe zone disabled.");
			return;
		}

		sendMessageParsed("<i>Auto-claiming of safe zone enabled.");

		FLocation playerFlocation = new FLocation(fme);
		
		if (!Board.getFactionAt(playerFlocation).isSafeZone())
		{
			Board.setFactionAt(Factions.i.getSafeZone(), playerFlocation);
			sendMessageParsed("<i>This land is now a safe zone.");
		}
	}
	
}
