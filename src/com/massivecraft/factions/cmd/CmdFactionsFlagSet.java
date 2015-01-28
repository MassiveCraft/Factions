package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.cmd.arg.ARMFlag;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsFlagChange;
import com.massivecraft.massivecore.cmd.arg.ARBoolean;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsFlagSet extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFlagSet()
	{
		// Aliases
		this.addAliases("set");
		
		// Args
		this.addRequiredArg("flag");
		this.addRequiredArg("yes/no");
		this.addOptionalArg("faction", "you");
		
		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.FLAG_SET.node));
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		MFlag flag = this.arg(0, ARMFlag.get());
		if (flag == null) return;
		Boolean value = this.arg(1, ARBoolean.get());
		if (value == null) return;
		Faction faction = this.arg(2, ARFaction.get(), msenderFaction);
		if (faction == null) return;
		
		// Do the sender have the right to change flags for this faction?
		if ( ! MPerm.getPermFlags().has(msender, faction, true)) return;
		
		// Is this flag editable?
		if (!msender.isUsingAdminMode() && ! flag.isEditable())
		{
			msg("<b>The flag <h>%s <b>is not editable.", flag.getName());
			return;
		}
		
		// Event
		EventFactionsFlagChange event = new EventFactionsFlagChange(sender, faction, flag, value);
		event.run();
		if (event.isCancelled()) return;
		value = event.isNewValue();
		
		// No change 
		if (faction.getFlag(flag) == value)
		{
			msg("%s <i>already has %s <i>set to %s<i>.", faction.describeTo(msender), flag.getStateDesc(value, false, true, true, false, true), flag.getStateDesc(value, true, true, false, false, false));
			return;
		}
		
		// Apply
		faction.setFlag(flag, value);
		
		// Inform
		String stateInfo = flag.getStateDesc(faction.getFlag(flag), true, false, true, true, true);
		if (msender.getFaction() != faction)
		{
			// Send message to sender
			msg("<h>%s <i>set a flag for <h>%s<i>.", msender.describeTo(msender, true), faction.describeTo(msender, true));
			sendMessage(stateInfo);
		}
		faction.msg("<h>%s <i>set a flag for <h>%s<i>.", msender.describeTo(faction, true), faction.describeTo(faction, true));
		faction.sendMessage(stateInfo);
	}
	
}
