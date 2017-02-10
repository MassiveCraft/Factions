package com.massivecraft.factions.cmd;

import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.Bukkit;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.collections.MassiveMap;
import com.massivecraft.massivecore.command.Parameter;
import com.massivecraft.massivecore.pager.Pager;
import com.massivecraft.massivecore.pager.Stringifier;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;

public class CmdFactionsRelationWishes extends FactionsCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public CmdFactionsRelationWishes()
	{
		// Parameter
		this.addParameter(Parameter.getPage());
		this.addParameter(TypeFaction.get(), "faction", "you");
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

		// Pager Create
		final Pager<Entry<Faction, Rel>> pager = new Pager<>(this, "", page, new Stringifier<Entry<Faction, Rel>>()
		{
			@Override
			public String toString(Entry<Faction, Rel> item, int index)
			{
				Rel rel = item.getValue();
				Faction fac = item.getKey();
				return rel.getColor().toString() + rel.getName() + CmdFactionsRelationList.SEPERATOR + fac.describeTo(faction, true);
			}
		});

		Bukkit.getScheduler().runTaskAsynchronously(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				Map<Faction, Rel> realWishes = new MassiveMap<>();

				for (Entry<String, Rel> entry : faction.getRelationWishes().entrySet())
				{
					Rel rel = entry.getValue();
					Faction fac = FactionColl.get().getFixed(entry.getKey());
					if (fac == null) continue;

					// A wish is not a wish anymore if both factions have atleast equal "wishes"
					if (fac.getRelationTo(faction).isAtLeast(rel)) continue;
					realWishes.put(fac, rel);
				}

				// Pager Title
				pager.setTitle(Txt.parse("<white>%s's <green>Relation wishes <a>(%d)", faction.getName(), realWishes.size()));

				// Pager Items
				pager.setItems(MUtil.entriesSortedByValues(realWishes));

				// Pager Message
				pager.message();
			}
		});
	}
}
