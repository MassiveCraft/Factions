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
		// TODO: This error output is temporary. I need a way to detect what is going wrong.
		Rel r1 = null;
		Rel r2 = null;
		try
		{
			r1 = o1.getRole();
		}
		catch (Exception e)
		{
			System.out.println("Could not get role for o1: " + o1.getId());
			System.out.println("universe o1: " + o1.getUniverse());
			System.out.println("attached o1: " + o1.attached());
			System.out.println("Now dumping the data o1: " + Factions.get().gson.toJson(o1));
			e.printStackTrace();
		}
		
		try
		{
			r2 = o2.getRole();
		}
		catch (Exception e)
		{
			System.out.println("Could not get role for o2: " + o2.getId());
			System.out.println("universe o2: " + o2.getUniverse());
			System.out.println("attached o2: " + o2.attached());
			System.out.println("Now dumping the data o2: " + Factions.get().gson.toJson(o2));
			e.printStackTrace();
		}
		
		return r2.getValue() - r1.getValue();
	}

}
