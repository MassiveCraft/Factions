package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class CmdSafeunclaimall extends FCommand
{
	
	public CmdSafeunclaimall()
	{
		this.aliases.add("safeunclaimall");
		this.aliases.add("safedeclaimall");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("radius", "0");
		
		this.permission = Permission.MANAGE_SAFE_ZONE.node;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		
		this.setHelpShort("Unclaim all safezone land");
	}
	
	@Override
	public void perform()
	{
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}
		
		Board.unclaimAll(Factions.i.getSafeZone().getId());
		sendMessageParsed("<i>You unclaimed ALL safe zone land.");
	}
	
}
