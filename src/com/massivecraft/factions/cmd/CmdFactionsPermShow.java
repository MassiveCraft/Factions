package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARMPerm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.cmd.arg.ARList;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsPermShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsPermShow()
	{
		// Aliases
		this.addAliases("s", "show");
		
		// Args
		this.addOptionalArg("faction", "you");
		this.addOptionalArg("perms", "all");
		this.setErrorOnToManyArgs(false);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.PERM_SHOW.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Arg: Faction
		Faction faction = this.arg(0, ARFaction.get(), msenderFaction);
		if (faction == null) return;
		
		List<MPerm> perms = new ArrayList<MPerm>();
		
		// Case: Show All
		if ( ! this.argIsSet(1) || "all".equalsIgnoreCase(this.arg(1)))
		{
			for (MPerm mperm : MPerm.getAll())
			{
				if ( ! mperm.isVisible() && ! msender.isUsingAdminMode()) continue;
				perms.add(mperm);
			}
		}
		// Case: Show Some
		else
		{	
			// Arg perm. Maybe we should use ARSet but that is currently buggy.
			List<MPerm> mperms = this.arg(this.argConcatFrom(1), ARList.get(ARMPerm.get()));
			if (mperms == null) return;
			perms.addAll(mperms);
		}
		
		// Create messages
		List<String> messages = new ArrayList<String>();

		messages.add(Txt.titleize("Perm for " + faction.describeTo(msender, true)));
		messages.add(MPerm.getStateHeaders());
		for (MPerm mperm : perms)
		{
			messages.add(Txt.parse(mperm.getStateInfo(faction.getPermitted(mperm), true)));
		}
		
		// Send messages
		sendMessage(messages);
	}
	
}
