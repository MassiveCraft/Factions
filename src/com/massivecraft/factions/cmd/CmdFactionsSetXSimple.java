package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.arg.ARFaction;

public abstract class CmdFactionsSetXSimple extends CmdFactionsSetX
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetXSimple(boolean claim)
	{
		// Super
		super(claim);
		
		// Args
		if (claim)
		{
			this.addArg(ARFaction.get(), "faction", "you");
			this.setFactionArgIndex(0);
		}
	}
	
}
