package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeMPerm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.command.type.container.TypeSet;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPermShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermShow()
	{
		// Aliases
		this.addAliases("show");
		
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(TypeSet.get(TypeMPerm.get()), "perms", "all", true);
		
		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.PERM_SHOW.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Arg: Faction
		Faction faction = this.readArg(msenderFaction);
		Collection<MPerm> mperms = this.readArg(MPerm.getAll());

		// Create messages
		List<String> messages = new ArrayList<String>();

		messages.add(Txt.titleize("Perm for " + faction.describeTo(msender, true)));
		messages.add(MPerm.getStateHeaders());
		for (MPerm mperm : mperms)
		{
			messages.add(Txt.parse(mperm.getStateInfo(faction.getPermitted(mperm), true)));
		}
		
		// Send messages
		message(messages);
	}
	
}
