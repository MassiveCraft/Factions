package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Selector;
import com.massivecraft.factions.cmd.type.TypeSelector;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.mson.Mson;

import java.util.List;

public class CmdFactionsPermFor extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermFor()
	{
		// Parameters
		this.addParameter(TypeSelector.get(), "selector");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Parameter
		Selector selector = this.readArg();
		Faction faction = msender.getUsedFaction();
		
		// Create
		List<Mson> perms = new MassiveList<>();
		
		// Fill
		for (MPerm perm : faction.getPermittedFor(selector))
		{
			// Create inner
			Mson permDesc = mson(perm.getDesc(true, false));
			List<String> tooltip = Mson.toPlain(faction.getPermittedShow(perm, msender), true);
			
			// Add
			perms.add(permDesc.tooltip(tooltip));
		}
		
		// Inform
		message(Mson.implodeCommaAndDot(perms));
	}
	
}
