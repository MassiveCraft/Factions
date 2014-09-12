package com.massivecraft.factions.entity;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.Aspect;
import com.massivecraft.massivecore.MassiveCore;

public class UConfColls extends XColls<UConfColl, UConf>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static UConfColls i = new UConfColls();
	public static UConfColls get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: COLLS
	// -------------------------------------------- //
	
	@Override
	public UConfColl createColl(String collName)
	{
		return new UConfColl(collName);
	}

	@Override
	public Aspect getAspect()
	{
		return Factions.get().getAspect();
	}
	
	@Override
	public String getBasename()
	{
		return Const.COLLECTION_UCONF;
	}
	
	@Override
	public UConf get2(Object worldNameExtractable)
	{
		UConfColl coll = this.get(worldNameExtractable);
		if (coll == null) return null;
		return coll.get(MassiveCore.INSTANCE);
	}
	
}

