package com.massivecraft.factions;

import java.util.Comparator;

import com.massivecraft.mcore.util.MUtil;

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
		
		// Null --> Low
		if (f1 == null && f2 == null) ret = 0;
		if (f1 == null) ret = -1;
		if (f2 == null) ret = +1;
		if (ret != 0) return ret;
		
		// None --> High
		if (f1.isNone() && f2.isNone()) ret = 0;
		if (f1.isNone()) ret = +1;
		if (f2.isNone()) ret = -1;
		if (ret != 0) return ret;
		
		// Players Online --> High
		ret = f1.getFPlayersWhereOnline(true).size() - f2.getFPlayersWhereOnline(true).size();
		if (ret != 0) return ret;
		
		// Players Total --> High
		ret = f1.getFPlayers().size() - f2.getFPlayers().size();
		if (ret != 0) return ret;
		
		// Tie by Id
		return MUtil.compare(f1.getId(), f2.getId());
	}

}
