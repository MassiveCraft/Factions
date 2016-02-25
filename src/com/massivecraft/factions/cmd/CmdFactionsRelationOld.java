package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;

import com.massivecraft.massivecore.command.Visibility;
import com.massivecraft.massivecore.util.MUtil;

public class CmdFactionsRelationOld extends FactionsCommand
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	public final String relName;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsRelationOld(String rel)
	{
		// Fields
		this.relName = rel.toLowerCase();

		// Aliases
		this.addAliases(relName);

		// Parameters
		this.addParameter(TypeFaction.get(), "faction");

		// Visibility
		this.setVisibility(Visibility.INVISIBLE);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform()
	{
		CmdFactions.get().cmdFactionsRelation.cmdFactionsRelationSet.execute(sender, MUtil.list(this.relName, this.argAt(0)));
	}

}
