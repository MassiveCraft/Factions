package com.massivecraft.factions.engine;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.PlayerRoleComparator;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.event.EventFactionsFactionShowAsync;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.PriorityLines;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EngineShow extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineShow i = new EngineShow();
	public static EngineShow get() { return i; }

	// -------------------------------------------- //
	// FACTION SHOW
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFactionShow(EventFactionsFactionShowAsync event)
	{
		final int tableCols = 4;
		final CommandSender sender = event.getSender();
		final MPlayer mplayer = event.getMPlayer();
		final Faction faction = event.getFaction();
		final boolean normal = faction.isNormal();
		final Map<String, PriorityLines> idPriorityLiness = event.getIdPriorityLiness();
		String none = Txt.parse("<silver><italic>none");

		// ID
		if (mplayer.isOverriding())
		{
			show(idPriorityLiness, Const.SHOW_ID_FACTION_ID, Const.SHOW_PRIORITY_FACTION_ID, "ID", faction.getId());
		}

		// DESCRIPTION
		show(idPriorityLiness, Const.SHOW_ID_FACTION_DESCRIPTION, Const.SHOW_PRIORITY_FACTION_DESCRIPTION, "Description", faction.getDescription());

		// SECTION: NORMAL
		if (normal)
		{
			// AGE
			long ageMillis = faction.getCreatedAtMillis() - System.currentTimeMillis();
			LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
			String ageDesc = TimeDiffUtil.formatedVerboose(ageUnitcounts, "<i>");
			show(idPriorityLiness, Const.SHOW_ID_FACTION_AGE, Const.SHOW_PRIORITY_FACTION_AGE, "Age", ageDesc);

			// FLAGS
			// We display all editable and non default ones. The rest we skip.
			List<String> flagDescs = new LinkedList<String>();
			for (Entry<MFlag, Boolean> entry : faction.getFlags().entrySet())
			{
				final MFlag mflag = entry.getKey();
				if (mflag == null) continue;

				final Boolean value = entry.getValue();
				if (value == null) continue;

				if ( ! mflag.isInteresting(value)) continue;

				String flagDesc = Txt.parse(value ? "<g>" : "<b>") + mflag.getName();
				flagDescs.add(flagDesc);
			}
			String flagsDesc = Txt.parse("<silver><italic>default");
			if ( ! flagDescs.isEmpty())
			{
				flagsDesc = Txt.implode(flagDescs, Txt.parse(" <i>| "));
			}
			show(idPriorityLiness, Const.SHOW_ID_FACTION_FLAGS, Const.SHOW_PRIORITY_FACTION_FLAGS, "Flags", flagsDesc);

			// POWER
			double powerBoost = faction.getPowerBoost();
			String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
			String powerDesc = Txt.parse("%d/%d/%d%s", faction.getLandCount(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);
			show(idPriorityLiness, Const.SHOW_ID_FACTION_POWER, Const.SHOW_PRIORITY_FACTION_POWER, "Land / Power / Maxpower", powerDesc);

			// SECTION: ECON
			if (Econ.isEnabled())
			{
				// LANDVALUES
				List<String> landvalueLines = new LinkedList<String>();
				long landCount = faction.getLandCount();
				for (EventFactionsChunkChangeType type : EventFactionsChunkChangeType.values())
				{
					Double money = MConf.get().econChunkCost.get(type);
					if (money == null) continue;
					if (money == 0) continue;
					money *= landCount;

					String word = "Cost";
					if (money <= 0)
					{
						word = "Reward";
						money *= -1;
					}

					String key = Txt.parse("Total Land %s %s", type.toString().toLowerCase(), word);
					String value = Txt.parse("<h>%s", Money.format(money));
					String line = show(key, value);
					landvalueLines.add(line);
				}
				idPriorityLiness.put(Const.SHOW_ID_FACTION_LANDVALUES, new PriorityLines(Const.SHOW_PRIORITY_FACTION_LANDVALUES, landvalueLines));

				// BANK
				if (MConf.get().bankEnabled)
				{
					double bank = Money.get(faction);
					String bankDesc = Txt.parse("<h>%s", Money.format(bank, true));
					show(idPriorityLiness, Const.SHOW_ID_FACTION_BANK, Const.SHOW_PRIORITY_FACTION_BANK, "Bank", bankDesc);
				}
			}
		}

		// FOLLOWERS
		List<String> followerLines = new ArrayList<String>();

		List<String> followerNamesOnline = new ArrayList<String>();
		List<String> followerNamesOffline = new ArrayList<String>();

		List<MPlayer> followers = faction.getMPlayers();
		Collections.sort(followers, PlayerRoleComparator.get());
		for (MPlayer follower : followers)
		{
			if (follower.isOnline(sender))
			{
				followerNamesOnline.add(follower.getNameAndTitle(mplayer));
			}
			else if (normal)
			{
				// For the non-faction we skip the offline members since they are far to many (infinite almost)
				followerNamesOffline.add(follower.getNameAndTitle(mplayer));
			}
		}

		String headerOnline = Txt.parse("<a>Followers Online (%s):", followerNamesOnline.size());
		followerLines.add(headerOnline);
		if (followerNamesOnline.isEmpty())
		{
			followerLines.add(none);
		}
		else
		{
			followerLines.addAll(table(followerNamesOnline, tableCols));
		}

		if (normal)
		{
			String headerOffline = Txt.parse("<a>Followers Offline (%s):", followerNamesOffline.size());
			followerLines.add(headerOffline);
			if (followerNamesOffline.isEmpty())
			{
				followerLines.add(none);
			}
			else
			{
				followerLines.addAll(table(followerNamesOffline, tableCols));
			}
		}
		idPriorityLiness.put(Const.SHOW_ID_FACTION_FOLLOWERS, new PriorityLines(Const.SHOW_PRIORITY_FACTION_FOLLOWERS, followerLines));
	}

	public static String show(String key, String value)
	{
		return Txt.parse("<a>%s: <i>%s", key, value);
	}

	public static PriorityLines show(int priority, String key, String value)
	{
		return new PriorityLines(priority, show(key, value));
	}

	public static void show(Map<String, PriorityLines> idPriorityLiness, String id, int priority, String key, String value)
	{
		idPriorityLiness.put(id, show(priority, key, value));
	}

	public static List<String> table(List<String> strings, int cols)
	{
		List<String> ret = new ArrayList<String>();

		StringBuilder row = new StringBuilder();
		int count = 0;

		Iterator<String> iter = strings.iterator();
		while (iter.hasNext())
		{
			String string = iter.next();
			row.append(string);
			count++;

			if (iter.hasNext() && count != cols)
			{
				row.append(Txt.parse(" <i>| "));
			}
			else
			{
				ret.add(row.toString());
				row = new StringBuilder();
				count = 0;
			}
		}

		return ret;
	}

}
