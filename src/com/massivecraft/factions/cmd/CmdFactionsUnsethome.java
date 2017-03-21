package com.massivecraft.factions.cmd;

import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.event.EventFactionsHomeChange;
import com.massivecraft.massivecore.MassiveException;

public class CmdFactionsUnsethome extends FactionsCommandHome
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsUnsethome()
	{
		// Parameters
		this.addParameter(TypeFaction.get(), "faction", "you");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		Faction faction = this.readArg(msenderFaction);
		
		// Any and MPerm
		if ( ! MPerm.getPermSethome().has(msender, faction, true)) return;
		
		// NoChange
		if ( ! faction.hasHome())
		{
			msender.msg("<b><bold>(!)<reset> <i>%s <i>does already not have a home.", faction.describeTo(msender));
			return;
		}
		
		// Event
		EventFactionsHomeChange event = new EventFactionsHomeChange(sender, faction, null);
		event.run();
		if (event.isCancelled()) return;

		// Apply
		faction.setHome(null);
		
		// Inform
		faction.msg("<b><bold>(!)<reset> %s<i> unset the home for your faction.", msender.describeTo(msenderFaction, true));
		if (faction != msenderFaction)
		{
			msender.msg("<b><bold>(!)<reset> <i>You have unset the home for " + faction.getName(msender) + "<i>.");
		}
	}
	
}
