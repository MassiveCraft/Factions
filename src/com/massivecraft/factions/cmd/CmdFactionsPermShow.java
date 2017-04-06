package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeMPerm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.mson.Mson;

import java.util.List;

public class CmdFactionsPermShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermShow()
	{
		// Parameters
		this.addParameter(TypeMPerm.get(), "perm");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameter
		MPerm perm = this.readArg();
		Faction faction = msender.getUsedFaction();
		
		// Create
		List<Mson> message = faction.getPermittedShow(perm, msender);

		// Inform
		message(message);
	}
	
}
