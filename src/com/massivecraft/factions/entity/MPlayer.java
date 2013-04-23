package com.massivecraft.factions.entity;

import com.massivecraft.mcore.store.SenderEntity;

public class MPlayer extends SenderEntity<MPlayer>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static MPlayer get(Object oid)
	{
		return MPlayerColl.get().get(oid);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public MPlayer load(MPlayer that)
	{
		// TODO
		
		return this;
	}
	
	@Override
	public boolean isDefault()
	{
		// TODO
		//return false;
		
		return true;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	
}
