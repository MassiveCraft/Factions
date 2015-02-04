package com.massivecraft.factions.cmd;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARMFlag;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.massivecore.cmd.MassiveCommandException;
import com.massivecraft.massivecore.cmd.arg.ARList;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsFlagShow extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlagShow()
	{
		// Aliases
		this.addAliases("s", "show");
		
		// Args
		this.addOptionalArg("faction", "you");
		this.addOptionalArg("flags", "all");
		this.setErrorOnToManyArgs(false);
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.FLAG_SHOW.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveCommandException
	{
		// Arg: Faction
		Faction faction = this.arg(0, ARFaction.get(), msenderFaction);
		
		List<MFlag> flags = new ArrayList<MFlag>();
		
		// Case: Show All
		if ( ! this.argIsSet(1) || "all".equalsIgnoreCase(this.arg(1)))
		{
			for (MFlag mflag : MFlag.getAll())
			{
				if (!mflag.isVisible() && ! msender.isUsingAdminMode()) continue;
				flags.add(mflag);
			}
		}
		else
		{
			// Arg: MFlag. Maybe we should use ARSet but that is currently buggy.
			List<MFlag> mflags = this.arg(this.argConcatFrom(1), ARList.get(ARMFlag.get()));
			flags.addAll(mflags);
		}
		
		// Create messages
		List<String> messages = new ArrayList<String>();
		messages.add(Txt.titleize("Flag for " + faction.describeTo(msender, true)));
		for (MFlag mflag : flags)
		{
			messages.add(mflag.getStateDesc(faction.getFlag(mflag), true, true, true, true, true));
		}

		// Send messages
		sendMessage(messages);
	}
	
}
