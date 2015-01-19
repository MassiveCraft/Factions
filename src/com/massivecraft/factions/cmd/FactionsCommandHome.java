package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.req.ReqFactionHomesEnabled;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.cmd.VisibilityMode;

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
	public VisibilityMode getVisibilityMode()
	{
		return MConf.get().homesEnabled ? super.getVisibilityMode() : VisibilityMode.INVISIBLE;  
	}
	
}
