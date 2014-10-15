package com.massivecraft.factions.cmd;

import java.util.TreeSet;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsFactionShow;
import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.PriorityLines;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsFaction extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFaction()
	{
		// Aliases
		this.addAliases("f", "faction");

		// Args
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.FACTION.node));
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
		
		// Event
		EventFactionsFactionShow event = new EventFactionsFactionShow(sender, faction);
		event.run();
		if (event.isCancelled()) return;
		
		// Title
		msg(Txt.titleize("Faction " + faction.getName(msender)));
		
		// Lines
		TreeSet<PriorityLines> priorityLiness = new TreeSet<PriorityLines>(event.getIdPriorityLiness().values());
		for (PriorityLines priorityLines : priorityLiness)
		{
			sendMessage(priorityLines.getLines());
		}
	}
	
}
