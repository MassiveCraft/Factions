package com.massivecraft.factions;

import java.util.Comparator;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Named;

public class PlayerInactivityComparator implements Comparator<MPlayer>, Named
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static PlayerInactivityComparator i = new PlayerInactivityComparator();
	public static PlayerInactivityComparator get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: NAMED
	// -------------------------------------------- //
	
	@Override
	public String getName()
	{
		return "Time";
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

		// Online
		boolean o1 = m1.isOnline();
		boolean o2 = m2.isOnline();
		
		if (o1 && o2) return 0;
		else if (o1) return -1;
		else if (o2) return +1;
		
		// Inactivity Time
		long r1 = m1.getLastActivityMillis();
		long r2 = m2.getLastActivityMillis();
		
		return (int) (r1 - r2);
	}
	
}
