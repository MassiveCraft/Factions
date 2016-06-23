package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.massivecore.store.Coll;

public class MPermColl extends Coll<MPerm>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MPermColl i = new MPermColl();
	public static MPermColl get() { return i; }
	private MPermColl()
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
		MPerm.setupStandardPerms();
	}
	
	// -------------------------------------------- //
	// EXTRAS
	// -------------------------------------------- //
	
	public List<MPerm> getAll(boolean registered)
	{
		List<MPerm> ret = new ArrayList<MPerm>();
		for (MPerm mperm : this.getAll())
		{
			if (mperm.isRegistered() != registered) continue;
			ret.add(mperm);
		}
		return ret;
	}
	
}
