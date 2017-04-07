package com.massivecraft.factions.comparator;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.comparator.ComparatorAbstract;
import com.massivecraft.massivecore.comparator.ComparatorComparable;
import com.massivecraft.massivecore.util.IdUtil;
import org.bukkit.command.CommandSender;

import java.lang.ref.WeakReference;

public class ComparatorFactionList extends ComparatorAbstract<Faction>
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final WeakReference<CommandSender> watcher;
	public CommandSender getWatcher() { return this.watcher.get(); }

	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	public static ComparatorFactionList get(Object watcherObject) { return new ComparatorFactionList(watcherObject); }
	public ComparatorFactionList(Object watcherObject)
	{
		this.watcher = new WeakReference<>(IdUtil.getSender(watcherObject));
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public int compareInner(Faction f1, Faction f2)
	{
		// None a.k.a. Wilderness
		if (f1.isNone() && f2.isNone()) return 0;
		if (f1.isNone()) return -1;
		if (f2.isNone()) return 1;
		
		// Players Online
		int ret = f2.getMPlayersWhereOnlineTo(this.getWatcher()).size() - f1.getMPlayersWhereOnlineTo(this.getWatcher()).size();
		if (ret != 0) return ret;
		
		// Players Total
		ret = f2.getMPlayers().size() - f1.getMPlayers().size();
		if (ret != 0) return ret;
		
		// Tie by Id
		return ComparatorComparable.get().compare(f1.getId(), f2.getId());
	}

}
