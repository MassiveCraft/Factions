package com.massivecraft.factions;

import java.util.Comparator;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.util.MUtil;

public class FactionListComparator implements Comparator<Faction>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionListComparator i = new FactionListComparator();
	public static FactionListComparator get() { return i; }
	
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
		ret = f2.getUPlayersWhereOnline(true).size() - f1.getUPlayersWhereOnline(true).size();
		if (ret != 0) return ret;
		
		// Players Total
		ret = f2.getUPlayers().size() - f1.getUPlayers().size();
		if (ret != 0) return ret;
		
		// Tie by Id
		return MUtil.compare(f1.getId(), f2.getId());
	}

}
