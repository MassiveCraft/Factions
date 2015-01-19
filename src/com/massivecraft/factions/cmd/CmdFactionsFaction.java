package com.massivecraft.factions.cmd;

import java.util.TreeSet;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsFactionShowAsync;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.massivecore.PriorityLines;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.mixin.Mixin;
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
		final Faction faction = this.arg(0, ARFaction.get(), msenderFaction);
		if (faction == null) return;
		
		final CommandSender sender = this.sender;
		
		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				// Event
				EventFactionsFactionShowAsync event = new EventFactionsFactionShowAsync(sender, faction);
				event.run();
				if (event.isCancelled()) return;
				
				// Title
				Mixin.messageOne(sender, Txt.titleize("Faction " + faction.getName(msender)));
				
				// Lines
				TreeSet<PriorityLines> priorityLiness = new TreeSet<PriorityLines>(event.getIdPriorityLiness().values());
				for (PriorityLines priorityLines : priorityLiness)
				{
					Mixin.messageOne(sender, priorityLines.getLines());
				}
			}
		});
		
	}
	
}
