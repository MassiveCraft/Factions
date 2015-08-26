package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.cmd.MassiveCommand;

public class FactionsCommand extends MassiveCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public MPlayer msender;
	public Faction msenderFaction;
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void fixSenderVars()
	{
		this.msender = MPlayer.get(sender);
		this.msenderFaction = this.msender.getFaction();
	}
	
	
	@Override
	public void unsetSenderVars()
	{
		this.msender = null;
		this.msenderFaction = null;
	}
	
}
