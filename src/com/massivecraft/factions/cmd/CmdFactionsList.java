package com.massivecraft.factions.cmd;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import com.massivecraft.factions.FactionListComparator;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.cmd.arg.ARInteger;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.pager.PagerSimple;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsList extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsList()
	{
		// Aliases
		this.addAliases("l", "list");

		// Args
		this.addArg(ARInteger.get(), "page", "1");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.LIST.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		final int pageHumanBased = this.readArg(1);
		
		// NOTE: The faction list is quite slow and mostly thread safe.
		// We run it asynchronously to spare the primary server thread.
		final CommandSender sender = this.sender;
		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				// Create Pager
				final List<Faction> factions = FactionColl.get().getAll(null, FactionListComparator.get());
				final PagerSimple<Faction> pager = new PagerSimple<Faction>(factions, sender);
				
				// Use Pager
				List<String> messages = pager.getPageTxt(pageHumanBased, "Faction List", new Stringifier<Faction>() {
					@Override
					public String toString(Faction faction, int index)
					{
						if (faction.isNone())
						{
							return Txt.parse("<i>Factionless<i> %d online", FactionColl.get().getNone().getMPlayersWhereOnline(true).size());
						}
						else
						{
							return Txt.parse("%s<i> %d/%d online, %d/%d/%d",
								faction.getName(msender),
								faction.getMPlayersWhereOnline(true).size(),
								faction.getMPlayers().size(),
								faction.getLandCount(),
								faction.getPowerRounded(),
								faction.getPowerMaxRounded()
							);
						}
					}
				});
				
				// Send Messages
				Mixin.messageOne(sender, messages);
			}
		});
	}
	
}
