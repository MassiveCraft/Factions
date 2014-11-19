package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;

public abstract class CmdFactionsSetXAll extends CmdFactionsSetX
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetXAll(boolean claim)
	{
		// Super
		super(claim);
		
		// Args
		this.addRequiredArg("all|map");
		this.addRequiredArg("faction");
		if (claim)
		{
			this.addRequiredArg("newfaction");
			this.setFactionArgIndex(2);
		}
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public Faction getOldFaction()
	{
		return this.arg(1, ARFaction.get());
	}
	
}
