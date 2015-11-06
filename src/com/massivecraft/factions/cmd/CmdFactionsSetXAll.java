package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;

public abstract class CmdFactionsSetXAll extends CmdFactionsSetX
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsSetXAll(boolean claim)
	{
		// Super
		super(claim);
		
		// Parameters
		this.addParameter(TypeString.get(), "all|map");
		this.addParameter(TypeFaction.get(), "faction");
		if (claim)
		{
			this.addParameter(TypeFaction.get(), "newfaction");
			this.setFactionArgIndex(2);
		}
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public Faction getOldFaction() throws MassiveException
	{
		return this.readArgAt(1);
	}
	
}
