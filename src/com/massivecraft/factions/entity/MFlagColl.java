package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.store.Coll;

import java.util.ArrayList;
import java.util.List;

public class MFlagColl extends Coll<MFlag>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MFlagColl i = new MFlagColl();
	public static MFlagColl get() { return i; }
	private MFlagColl()
	{
		this.setLowercasing(true);
	}

	// -------------------------------------------- //
	// STACK TRACEABILITY
	// -------------------------------------------- //
	
	@Override
	public void onTick()
	{
		super.onTick();
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void setActive(boolean active)
	{
		super.setActive(active);
		if (!active) return;
		MFlag.setupStandardFlags();
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public List<MFlag> getAll(boolean registered)
	{
		// Create
		List<MFlag> ret = new ArrayList<>();
		
		// Fill
		for (MFlag mflag : this.getAll())
		{
			if (mflag.isRegistered() != registered) continue;
			ret.add(mflag);
		}
		
		// Return
		return ret;
	}
	
}
