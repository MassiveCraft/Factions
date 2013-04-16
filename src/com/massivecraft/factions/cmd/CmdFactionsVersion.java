package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;


public class CmdFactionsVersion extends FCommand
{
	public CmdFactionsVersion()
	{
		this.aliases.add("version");
		
		this.permission = Perm.VERSION.node;
	}

	@Override
	public void perform()
	{
		msg("<i>You are running "+Factions.get().getDescription().getFullName());
	}
}
