package com.massivecraft.factions.commands;

import com.massivecraft.factions.struct.Permission;

public class CmdLock extends FCommand {
	
	// TODO: This solution needs refactoring.
	/*
	   factions.lock:
    description: use the /f lock [on/off] command to temporarily lock the data files from being overwritten
    default: op
	 */
	
	public CmdLock()
	{
		super();
		this.aliases.add("lock");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flipp");
		
		this.permission = Permission.COMMAND_LOCK.node;
		
		senderMustBePlayer = false;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		setIsLocked(this.argAsBool(0, ! isLocked()));
		
		if( isLocked() )
		{
			sendMessageParsed("<i>Factions is now locked");
		}
		else
		{
			sendMessageParsed("<i>Factions in now unlocked");
		}
	}
	
}
