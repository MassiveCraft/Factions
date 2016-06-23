package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.massivecore.store.Coll;

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
		if ( ! active) return;
		MFlag.setupStandardFlags();
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public List<MFlag> getAll(boolean registered)
	{
		List<MFlag> ret = new ArrayList<MFlag>();
		for (MFlag mflag : this.getAll())
		{
			if (mflag.isRegistered() != registered) continue;
			ret.add(mflag);
		}
		return ret;
	}
	
}
