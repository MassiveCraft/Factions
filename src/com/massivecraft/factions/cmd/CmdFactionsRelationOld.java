package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.MassiveException;
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
		this.setSetupEnabled(false);

		// Aliases
		this.addAliases(relName);

		// Parameters
		this.addParameter(TypeFaction.get(), "faction", true);

		// Visibility
		this.setVisibility(Visibility.INVISIBLE);
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public void perform() throws MassiveException
	{
		// Arguments
		Faction faction = this.readArg();
		
		// Apply
		CmdFactions.get().cmdFactionsRelation.cmdFactionsRelationSet.execute(sender, MUtil.list(
			faction.getId(),
			this.relName
		));
	}

}
