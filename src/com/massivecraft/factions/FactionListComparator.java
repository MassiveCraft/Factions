package com.massivecraft.factions;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.comparator.ComparatorComparable;
import com.massivecraft.massivecore.util.IdUtil;
import org.bukkit.command.CommandSender;

import java.lang.ref.WeakReference;
import java.util.Comparator;

public class FactionListComparator implements Comparator<Faction>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final WeakReference<CommandSender> watcher;
	public CommandSender getWatcher() { return this.watcher.get(); }

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	public static FactionListComparator get(Object watcherObject) { return new FactionListComparator(watcherObject); }
	public FactionListComparator(Object watcherObject)
	{
		this.watcher = new WeakReference<>(IdUtil.getSender(watcherObject));
	}

	// -------------------------------------------- //
	// OVERRIDE: COMPARATOR
	// -------------------------------------------- //
	
	@Override
	public int compare(Faction f1, Faction f2)
	{
		int ret = 0;
		
		// Null
		if (f1 == null && f2 == null) ret = 0;
		if (f1 == null) ret = -1;
		if (f2 == null) ret = +1;
		if (ret != 0) return ret;
		
		// None a.k.a. Wilderness
		if (f1.isNone() && f2.isNone()) ret = 0;
		if (f1.isNone()) ret = -1;
		if (f2.isNone()) ret = +1;
		if (ret != 0) return ret;
		
		// Players Online
		ret = f2.getMPlayersWhereOnlineTo(this.getWatcher()).size() - f1.getMPlayersWhereOnlineTo(this.getWatcher()).size();
		if (ret != 0) return ret;
		
		// Players Total
		ret = f2.getMPlayers().size() - f1.getMPlayers().size();
		if (ret != 0) return ret;
		
		// Tie by Id
		return ComparatorComparable.get().compare(f1.getId(), f2.getId());
	}

}
