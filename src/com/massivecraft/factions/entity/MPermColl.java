package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.List;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;

public class MPermColl extends Coll<MPerm>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static MPermColl i = new MPermColl();
	public static MPermColl get() { return i; }
	private MPermColl()
	{
		super(Const.COLLECTION_MPERM, MPerm.class, MStore.getDb(), Factions.get());
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
	public void init()
	{
		super.init();
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
