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
import com.massivecraft.factions.event.FactionsEventNameChange;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;

public class CmdFactionsName extends FCommand
{
	public CmdFactionsName()
	{
		this.addAliases("name");
		
		this.addRequiredArg("new name");
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.NAME.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}
	
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
		FactionsEventNameChange event = new FactionsEventNameChange(sender, usenderFaction, newName);
		event.run();
		if (event.isCancelled()) return;
		newName = event.getNewName();

		// Apply
		String oldName = usenderFaction.getName();
		usenderFaction.setName(newName);

		// Inform
		usenderFaction.msg("%s<i> changed your faction name to %s", usender.describeTo(usenderFaction, true), usenderFaction.getName(usenderFaction));
		for (Faction faction : FactionColls.get().get(usenderFaction).getAll())
		{
			if (faction == usenderFaction)
			{
				continue;
			}
			faction.msg("<i>The faction %s<i> changed their name to %s.", usender.getColorTo(faction)+oldName, usenderFaction.getName(faction));
		}
	}
	
}
