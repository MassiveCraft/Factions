package com.massivecraft.factions.cmd;

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
			this.addOptionalArg("faction", "you");
			this.setFactionArgIndex(0);
		}
	}
	
}
