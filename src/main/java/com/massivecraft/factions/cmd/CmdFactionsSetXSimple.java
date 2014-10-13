package com.massivecraft.factions.cmd;

public abstract class CmdFactionsSetXSimple extends CmdFactionsSetX
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetXSimple()
	{
		// Args
		this.addOptionalArg("faction", "you");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public int getFactionArgIndex()
	{
		return 0;
	}
	
}
