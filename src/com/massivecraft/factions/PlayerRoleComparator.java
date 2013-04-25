package com.massivecraft.factions;

import java.util.Comparator;

import com.massivecraft.factions.entity.UPlayer;

public class PlayerRoleComparator implements Comparator<UPlayer>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static PlayerRoleComparator i = new PlayerRoleComparator();
	public static PlayerRoleComparator get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: COMPARATOR
	// -------------------------------------------- //
	
	@Override
	public int compare(UPlayer o1, UPlayer o2)
	{
		int ret = 0;
		
		// Null
		if (o1 == null && o2 == null) ret = 0;
		if (o1 == null) ret = -1;
		if (o2 == null) ret = +1;
		if (ret != 0) return ret;
		
		// Rank
		return o2.getRole().getValue() - o1.getRole().getValue();
	}

}
