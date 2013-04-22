package com.massivecraft.factions.entity;

import com.massivecraft.mcore.MCore;
import com.massivecraft.mcore.store.Entity;

public class MConf extends Entity<MConf>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static MConf get()
	{
		return MConfColl.get().get(MCore.INSTANCE);
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	

}