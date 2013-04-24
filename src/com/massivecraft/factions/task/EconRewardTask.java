package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.mcore.ModuloRepeatTask;

public class EconRewardTask extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EconRewardTask i = new EconRewardTask();
	public static EconRewardTask get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: MODULO REPEAT TASK
	// -------------------------------------------- //
	
	@Override
	public long getDelayMillis()
	{
		return MConf.get().taskEconMillis;
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		MConf.get().taskEconMillis = delayMillis;
	}
	
	@Override
	public void invoke()
	{
		for (FactionColl coll : FactionColls.get().getColls())
		{
			coll.econLandRewardRoutine();
		}
	}
	
}
