package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqFactionHomesEnabled;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.command.Visibility;

public class FactionsCommandHome extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsCommandHome()
	{
		this.addRequirements(ReqFactionHomesEnabled.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public Visibility getVisibility()
	{
		return MConf.get().homesEnabled ? super.getVisibility() : Visibility.INVISIBLE;  
	}
	
}
