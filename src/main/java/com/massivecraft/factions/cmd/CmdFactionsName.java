package com.massivecraft.factions.cmd;

import java.util.ArrayList;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.event.EventFactionsNameChange;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsName extends FCommand
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

		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.NAME.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Arg
		String newName = this.arg(0);
		
		// TODO does not first test cover selfcase?
		
		FactionColl factionColl = FactionColls.get().get(usenderFaction);
		if (factionColl.isNameTaken(newName) && ! MiscUtil.getComparisonString(newName).equals(usenderFaction.getComparisonName()))
		{
			msg("<b>That name is already taken");
			return;
		}

		ArrayList<String> errors = new ArrayList<String>();
		errors.addAll(factionColl.validateName(newName));
		if (errors.size() > 0)
		{
			sendMessage(errors);
			return;
		}

		// Event
		EventFactionsNameChange event = new EventFactionsNameChange(sender, usenderFaction, newName);
		event.run();
		if (event.isCancelled()) return;
		newName = event.getNewName();

		// Apply
		String oldName = usenderFaction.getName();
		usenderFaction.setName(newName);

		// Inform
		usenderFaction.msg("%s<i> changed your faction name to %s", usender.describeTo(usenderFaction, true), usenderFaction.getName(usenderFaction));
		
		if (!UConf.get(usender).broadcastNameChange) return;
		for (Faction faction : FactionColls.get().get(usenderFaction).getAll())
		{
			if (faction == usenderFaction)
			{
				continue;
			}
			faction.msg("<i>The player %s<i> changed their faction name from %s<i> to %s<i>.", usender.describeTo(faction, true), usender.getColorTo(faction)+oldName, usenderFaction.getName(faction));
		}
	}
	
}
