package com.massivecraft.factions.update;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.Aspect;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.store.Colls;

public class OldConfColls extends Colls<OldConfColl, OldConf>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static OldConfColls i = new OldConfColls();
	public static OldConfColls get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: COLLS
	// -------------------------------------------- //
	
	@Override
	public OldConfColl createColl(String collName)
	{
		return new OldConfColl(collName);
	}

	@Override
	public Aspect getAspect()
	{
		return Factions.get().getAspect();
	}
	
	@Override
	public String getBasename()
	{
		return "factions_uconf";
	}
	
	@Override
	public OldConf get2(Object worldNameExtractable)
	{
		OldConfColl coll = this.get(worldNameExtractable);
		if (coll == null) return null;
		return coll.get(MassiveCore.INSTANCE);
	}
	
}
