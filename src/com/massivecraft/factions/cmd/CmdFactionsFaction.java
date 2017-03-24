package com.massivecraft.factions.cmd;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsFactionShowAsync;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.PriorityLines;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import java.util.TreeSet;

public class CmdFactionsFaction extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsFaction()
	{
		// Aliases
		this.addAliases("f", "show", "who");

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
		final Faction faction = this.readArg(msenderFaction);
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
				MixinMessage.get().messageOne(sender, Txt.titleize("Faction " + faction.getName(msender)));
				
				// Lines
				TreeSet<PriorityLines> priorityLiness = new TreeSet<>(event.getIdPriorityLiness().values());
				for (PriorityLines priorityLines : priorityLiness)
				{
					MixinMessage.get().messageOne(sender, priorityLines.getLines());
				}
			}
		});
		
	}
	
}
