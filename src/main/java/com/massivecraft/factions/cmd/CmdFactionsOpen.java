package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.event.EventFactionsOpenChange;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsOpen extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsOpen()
	{
		// Aliases
		this.addAliases("open");

		// Args
		this.addOptionalArg("yes/no", "toggle");

		// Requirements
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.OPEN.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.OFFICER));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Boolean newOpen = this.arg(0, ARBoolean.get(), !usenderFaction.isOpen());
		if (newOpen == null) return;

		// Event
		EventFactionsOpenChange event = new EventFactionsOpenChange(sender, usenderFaction, newOpen);
		event.run();
		if (event.isCancelled()) return;
		newOpen = event.isNewOpen();
		
		// Apply
		usenderFaction.setOpen(newOpen);
		
		// Inform
		String descTarget = usenderFaction.isOpen() ? "open" : "closed";
		usenderFaction.msg("%s<i> changed the faction to <h>%s<i>.", usender.describeTo(usenderFaction, true), descTarget);
	}
	
}
