package com.massivecraft.factions.commands;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.struct.Permission;

public class CmdWarunclaimall extends FCommand
{
	
	public CmdWarunclaimall()
	{
		this.aliases.add("warunclaimall");
		this.aliases.add("wardeclaimall");
		
		//this.requiredArgs.add("");
		//this.optionalArgs.put("", "");
		
		this.permission = Permission.MANAGE_WAR_ZONE.node;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
		
		this.setHelpShort("unclaim all warzone land");
	}
	
	@Override
	public void perform()
	{
		
		if( isLocked() )
		{
			sendLockMessage();
			return;
		}
		
		Board.unclaimAll(Factions.i.getWarZone().getId());
		sendMessageParsed("<i>You unclaimed ALL war zone land.");
	}
	
}
