package com.massivecraft.factions.commands;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.Permission;


public class FCommandBypass extends FCommand
{
	public FCommandBypass()
	{
		super();
		this.aliases.add("bypass");
		
		//this.requiredArgs.add("");
		this.optionalArgs.put("on/off", "flipp");
		
		this.permission = Permission.ADMIN_BYPASS.node;
		
		senderMustBePlayer = true;
		senderMustBeMember = false;
		senderMustBeModerator = false;
		senderMustBeAdmin = false;
	}
	
	@Override
	public void perform()
	{
		// TODO: Move this to a transient field in the model??
		if ( ! Conf.adminBypassPlayers.contains(fme.getName()))
		{
			Conf.adminBypassPlayers.add(fme.getName());
			fme.sendMessageParsed("<i>You have enabled admin bypass mode. You will be able to build or destroy anywhere.");
			P.p.log(fme.getName() + " has ENABLED admin bypass mode.");
		}
		else
		{
			Conf.adminBypassPlayers.remove(fme.getName());
			fme.sendMessageParsed("<i>You have disabled admin bypass mode.");
			P.p.log(fme.getName() + " DISABLED admin bypass mode.");
		}
	}
}
