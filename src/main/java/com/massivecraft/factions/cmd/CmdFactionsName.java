package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsName extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsName()
	{
		// Aliases
		this.addAliases("name");

		// Args
		this.addRequiredArg("new name");
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.NAME.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		String newName = this.arg(0);
		
		Faction faction = this.arg(1, ARFaction.get(), msenderFaction);
		if (faction == null) return;
		
		// MPerm
		if ( ! MPerm.getPermName().has(msender, faction, true)) return;
		
		// TODO does not first test cover selfcase?
		if (FactionColl.get().isNameTaken(newName) && ! MiscUtil.getComparisonString(newName).equals(faction.getComparisonName()))
		{
			msg("<b>That name is already taken");
			return;
		}

		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(FactionColl.get().validateName(newName));
		if (errors.size() > 0)
		{
			sendMessage(errors);
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
		faction.msg("%s<i> changed your faction name to %s", msender.describeTo(faction, true), faction.getName(faction));
		if (msenderFaction != faction)
		{
			msg("<i>You changed the faction name to %s", faction.getName(msender));
		}
	}
	
}
