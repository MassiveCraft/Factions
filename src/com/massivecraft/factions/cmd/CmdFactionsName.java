package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.type.primitive.TypeString;

import java.util.ArrayList;

public class CmdFactionsName extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsName()
	{
		// Parameters
		this.addParameter(TypeString.get(), "new name");
		this.addParameter(TypeFaction.get(), "faction", "you");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		String newName = this.readArg();
		Faction faction = this.readArg(msenderFaction);
		
		// MPerm
		if ( ! MPerm.getPermName().has(msender, faction, true)) return;
		
		// TODO does not first test cover selfcase?
		if (FactionColl.get().isNameTaken(newName) && ! MiscUtil.getComparisonString(newName).equals(faction.getComparisonName()))
		{
			msg("<b>That name is already taken");
			return;
		}

		ArrayList<String> errors = new ArrayList<>();
		errors.addAll(FactionColl.get().validateName(newName));
		if (errors.size() > 0)
		{
			message(errors);
			return;
		}

		// Event
		EventFactionsNameChange event = new EventFactionsNameChange(sender, faction, newName);
		event.run();
		if (event.isCancelled()) return;
		newName = event.getNewName();

		// Apply
		faction.setName(newName);

		// Inform
		faction.msg("<b><bold>(!)<reset> %s<i> changed your faction name to %s", msender.describeTo(faction, true), faction.getName(faction));
		if (msenderFaction != faction)
		{
			msg("<b><bold>(!)<reset> <i>You changed the faction name to %s", faction.getName(msender));
		}
	}
	
}
