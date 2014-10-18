package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARMFlag;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsFlagChange;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsFlag extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlag()
	{
		// Aliases
		this.addAliases("flag");

		// Args
		this.addOptionalArg("faction", "you");
		this.addOptionalArg("flag", "all");
		this.addOptionalArg("yes/no", "read");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.FLAG.node));
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
		
		// Case: Show All
		if ( ! this.argIsSet(1))
		{
			msg(Txt.titleize("Flags for " + faction.describeTo(msender, true)));
			for (MFlag mflag : MFlag.getAll())
			{
				if (!mflag.isVisible() && !msender.isUsingAdminMode()) continue;
				msg(mflag.getStateDesc(faction.getFlag(mflag), true, true, true, true, false));
			}
			return;
		}
		
		// Arg: MFlag
		MFlag mflag = this.arg(1, ARMFlag.get());
		if (mflag == null) return;
		
		// Case: Show One
		if ( ! this.argIsSet(2))
		{
			msg(Txt.titleize("Flag for " + faction.describeTo(msender, true)));
			msg(mflag.getStateDesc(faction.getFlag(mflag), true, true, true, true, false));
			return;
		}
		
		// Do the sender have the right to change flags for this faction?
		if ( ! MPerm.getPermFlags().has(msender, faction, true)) return;
		
		// Is this flag editable?
		if (!msender.isUsingAdminMode() && !mflag.isEditable())
		{
			msg("<b>The flag <h>%s <b>is not editable.", mflag.getName());
			return;
		}
		
		// Arg: Target Value
		Boolean targetValue = this.arg(2, ARBoolean.get());
		if (targetValue == null) return;
		
		// Event
		EventFactionsFlagChange event = new EventFactionsFlagChange(sender, faction, mflag, targetValue);
		event.run();
		if (event.isCancelled()) return;
		targetValue = event.isNewValue();
		
		// Apply
		faction.setFlag(mflag, targetValue);
		
		// Inform
		String stateInfo = mflag.getStateDesc(faction.getFlag(mflag), true, false, true, true, true);
		if (msender.getFaction() != faction)
		{
			// Send message to sender
			msg("<h>%s <i>set a flag for <h>%s", msender.describeTo(msender, true), faction.describeTo(msender, true));
			msg(stateInfo);
		}
		faction.msg("<h>%s <i>set a flag for <h>%s", msender.describeTo(faction, true), faction.describeTo(faction, true));
		faction.msg(stateInfo);
	}
	
}
