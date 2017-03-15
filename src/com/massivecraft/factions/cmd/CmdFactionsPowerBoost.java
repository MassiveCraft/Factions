package com.massivecraft.factions.cmd;

public class CmdFactionsPowerBoost extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public CmdFactionsPowerBoostPlayer cmdFactionsPowerBoostPlayer = new CmdFactionsPowerBoostPlayer();
	public CmdFactionsPowerBoostFaction cmdFactionsPowerBoostFaction = new CmdFactionsPowerBoostFaction();
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPowerBoost()
	{
		// Child
		this.addChild(this.cmdFactionsPowerBoostPlayer);
		this.addChild(this.cmdFactionsPowerBoostFaction);
	}
	
}
