package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;

public class CmdFactionsPowerBoostFaction extends CmdFactionsPowerBoostAbstract
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPowerBoostFaction()
	{
		super(TypeFaction.get(), "faction");
	}
	
}
