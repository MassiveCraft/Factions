package com.massivecraft.factions.update;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Coll;
import com.massivecraft.massivecore.store.MStore;

public class OldConfColl extends Coll<OldConf>
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public OldConfColl(String name)
	{
		super(name, OldConf.class, MStore.getDb(), Factions.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void init()
	{
		super.init();
		this.get(MassiveCore.INSTANCE, true);
	}
	
}
