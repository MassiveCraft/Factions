package com.massivecraft.factions.comparator;

import java.util.Comparator;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Named;

public class ComparatorMPlayerRole implements Comparator<MPlayer>, Named
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ComparatorMPlayerRole i = new ComparatorMPlayerRole();
	public static ComparatorMPlayerRole get() { return i; }
	
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
		Rank r1 = m1.getRank();
		Rank r2 = m2.getRank();
		return r2.getOrder() - r1.getOrder();
	}
	
}
