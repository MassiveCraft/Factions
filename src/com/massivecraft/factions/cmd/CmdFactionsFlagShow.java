package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMFlag;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.container.TypeSet;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsFlagShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlagShow()
	{
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(TypeSet.get(TypeMFlag.get()), "flags", "all", true);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Arg: Faction
		Faction faction = this.readArg(msenderFaction);
		Collection<MFlag> mflags = this.readArg(MFlag.getAll());
		
		// Create messages
		List<Object> messages = new ArrayList<>();
		messages.add(Txt.titleize("Flag for " + faction.describeTo(msender, true)));
		for (MFlag mflag : mflags)
		{
			messages.add(mflag.getStateDesc(faction.getFlag(mflag), true, true, true, true, true));
		}

		// Send messages
		message(messages);
	}
	
}
