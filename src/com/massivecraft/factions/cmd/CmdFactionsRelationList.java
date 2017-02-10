package com.massivecraft.factions.cmd;

import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.cmd.type.TypeRelation;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.command.type.container.TypeSet;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRelationList extends FactionsCommand
{
	// -------------------------------------------- //
	// COSTANTS
	// -------------------------------------------- //

	public static final Set<Rel> RELEVANT_RELATIONS = new MassiveSet<Rel>(Rel.ENEMY, Rel.TRUCE, Rel.ALLY);
	public static final String SEPERATOR = Txt.parse("<silver>: ");

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsRelationList()
	{
		// Parameter
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
		this.addParameter(TypeSet.get(TypeRelation.get()), "relations", "all");
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform() throws MassiveException
	{
		// Args
		int page = this.readArg();
		final Faction faction = this.readArg(msenderFaction);
		final Set<Rel> relations = this.readArg(RELEVANT_RELATIONS);

		// Pager Create
		final Pager<String> pager = new Pager<String>(this, "", page, new Stringifier<String>()
		{
			@Override
			public String toString(String item, int index)
			{
				return item;
			}
		});

		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				// Prepare Items
				List<String> relNames = new MassiveList<>();
				for (Entry<Rel, List<String>> entry : FactionColl.get().getRelationNames(faction, relations).entrySet())
				{
					Rel relation = entry.getKey();
					String coloredName = relation.getColor().toString() + relation.getName();

					for (String name : entry.getValue())
					{
						relNames.add(coloredName + SEPERATOR + name);
					}
				}

				// Pager Title
				pager.setTitle(Txt.parse("<white>%s's <green>Relations <a>(%d)", faction.getName(), relNames.size()));

				// Pager Items
				pager.setItems(relNames);

				// Pager Message
				pager.message();
			}
		});
	}
	
}
