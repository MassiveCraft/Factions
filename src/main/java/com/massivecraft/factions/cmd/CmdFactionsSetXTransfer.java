package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;

public abstract class CmdFactionsSetXTransfer extends CmdFactionsSetX
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetXTransfer()
	{
		// Args
		this.addRequiredArg("all|map");
		this.addRequiredArg("old");
		this.addRequiredArg("new");
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public int getFactionArgIndex()
	{
		return 2;
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public Faction getOldFaction()
	{
		return this.arg(1, ARFaction.get());
	}
	
}
