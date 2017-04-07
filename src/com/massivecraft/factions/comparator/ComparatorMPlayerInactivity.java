package com.massivecraft.factions.comparator;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Named;
import com.massivecraft.massivecore.comparator.ComparatorAbstract;

public class ComparatorMPlayerInactivity extends ComparatorAbstract<MPlayer> implements Named
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static ComparatorMPlayerInactivity i = new ComparatorMPlayerInactivity();
	public static ComparatorMPlayerInactivity get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getName()
	{
		return "Time";
	}
	
	@Override
	public int compareInner(MPlayer m1, MPlayer m2)
	{
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
