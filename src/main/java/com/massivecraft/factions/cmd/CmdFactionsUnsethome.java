package com.massivecraft.factions.cmd;

import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsHomeChange;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;

public class CmdFactionsUnsethome extends FactionsCommandHome
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsUnsethome()
	{
		// Aliases
		this.addAliases("unsethome");

		// Args
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.UNSETHOME.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// Args
		Faction faction = this.arg(0, ARFaction.get(), msenderFaction);
		if (faction == null) return;
		
		// Other Perm
		if (faction != msenderFaction && !Perm.HOME_OTHER.has(sender, true)) return;
		
		// FPerm
		if ( ! FPerm.SETHOME.has(msender, faction, true)) return;
		
		// NoChange
		if ( ! faction.hasHome())
		{
			msender.msg("<i>%s <i>does already not have a home.", faction.describeTo(msender));
			return;
		}
		
		// Event
		EventFactionsHomeChange event = new EventFactionsHomeChange(sender, faction, null);
		event.run();
		if (event.isCancelled()) return;

		// Apply
		faction.setHome(null);
		
		// Inform
		faction.msg("%s<i> unset the home for your faction.", msender.describeTo(msenderFaction, true));
		if (faction != msenderFaction)
		{
			msender.msg("<i>You have unset the home for " + faction.getName(msender) + "<i>.");
		}
	}
	
}
