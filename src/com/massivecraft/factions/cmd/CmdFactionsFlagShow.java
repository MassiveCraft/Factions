package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMFlag;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.type.TypeAll;
import com.massivecraft.massivecore.cmd.type.TypeSet;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsFlagShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlagShow()
	{
		// Aliases
		this.addAliases("show");
		
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(TypeAll.get(TypeSet.get(TypeMFlag.get(), false)), "flags", "all", true);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.FLAG_SHOW.node));
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
		List<String> messages = new ArrayList<String>();
		messages.add(Txt.titleize("Flag for " + faction.describeTo(msender, true)));
		for (MFlag mflag : mflags)
		{
			messages.add(mflag.getStateDesc(faction.getFlag(mflag), true, true, true, true, true));
		}

		// Send messages
		message(messages);
	}
	
}
