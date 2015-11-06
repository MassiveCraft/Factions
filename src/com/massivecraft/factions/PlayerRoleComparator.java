package com.massivecraft.factions;

import java.util.Comparator;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Named;

public class PlayerRoleComparator implements Comparator<MPlayer>, Named
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static PlayerRoleComparator i = new PlayerRoleComparator();
	public static PlayerRoleComparator get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: NAMED
	// -------------------------------------------- //
	
	@Override
	public String getName()
	{
		return "Rank";
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COMPARATOR
	// -------------------------------------------- //
	
	@Override
	public int compare(MPlayer m1, MPlayer m2)
	{
		// Null
		if (m1 == null && m2 == null) return 0;
		else if (m1 == null) return -1;
		else if (m2 == null) return +1;
		
		// Rank
		Rel r1 = m1.getRole();
		Rel r2 = m2.getRole();
		return r2.getValue() - r1.getValue();
	}



}
