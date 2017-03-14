package com.massivecraft.factions.cmd;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.command.MassiveCommand;

public class FactionsCommand extends MassiveCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public MPlayer msender;
	public Faction msenderFaction;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public FactionsCommand()
	{
		this.setSetupEnabled(true);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void senderFields(boolean set)
	{
		this.msender = set ? MPlayer.get(sender) : null;
		this.msenderFaction = set ? this.msender.getFaction() : null;
	}

}
