package com.massivecraft.factions;

import java.util.Comparator;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Named;

public class PlayerPowerComparator implements Comparator<MPlayer>, Named
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static PlayerPowerComparator i = new PlayerPowerComparator();
	public static PlayerPowerComparator get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: NAMED
	// -------------------------------------------- //
	
	@Override
	public String getName()
	{
		return "Power";
	}
	
	// -------------------------------------------- //
	// OVERRIDE: COMPARATOR
	// -------------------------------------------- //
	
	@Override
	public int compare(MPlayer m1, MPlayer m2)
	{
		int ret = 0;
		
		// Null
		if (m1 == null && m2 == null) return 0;
		else if (m1 == null) return -1;
		else if (m2 == null) return +1;

		// Power
		int p1 = m1.getPowerRounded();
		int p2 = m2.getPowerRounded();
		ret = p1 - p2;
		if (ret != 0) return ret;
		
		// MaxPower
		int max1 = m1.getPowerMaxRounded();
		int max2 = m2.getPowerMaxRounded();
		ret = max1 - max2;
			
		return ret;
	}

}
