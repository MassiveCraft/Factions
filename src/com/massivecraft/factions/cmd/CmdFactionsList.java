package com.massivecraft.factions.cmd;

import java.util.List;

import org.bukkit.Bukkit;

import com.massivecraft.factions.FactionListComparator;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.requirement.RequirementHasPerm;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.predicate.Predicate;
import com.massivecraft.massivecore.predicate.PredicateAnd;
import com.massivecraft.massivecore.predicate.PredicateVisibleTo;
import com.massivecraft.massivecore.store.SenderColl;
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

		// Parameters
		this.addParameter(Parameter.getPage());

		// Requirements
		this.addRequirements(RequirementHasPerm.get(Perm.LIST.node));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		int page = this.readArg();
		final MPlayer msender = this.msender;
		final Predicate<MPlayer> onlinePredicate = PredicateAnd.get(SenderColl.PREDICATE_ONLINE, PredicateVisibleTo.get(sender));
		
		// NOTE: The faction list is quite slow and mostly thread safe.
		// We run it asynchronously to spare the primary server thread.
		
		// Pager Create
		final Pager<Faction> pager = new Pager<Faction>(this, "Faction List", page, new Stringifier<Faction>() {
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
						faction.getMPlayersWhere(onlinePredicate).size(),
						faction.getMPlayers().size(),
						faction.getLandCount(),
						faction.getPowerRounded(),
						faction.getPowerMaxRounded()
					);
				}
			}
		});
		
		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				// Pager Items
				final List<Faction> factions = FactionColl.get().getAll(FactionListComparator.get());
				pager.setItems(factions);
				
				// Pager Message
				pager.message();
			}
		});
	}
	
}
