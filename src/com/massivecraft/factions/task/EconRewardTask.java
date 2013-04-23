package com.massivecraft.factions.task;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.FactionColls;
import com.massivecraft.mcore.ModuloRepeatTask;
import com.massivecraft.mcore.util.TimeUnit;

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
		return (long) (ConfServer.econLandRewardTaskRunsEveryXMinutes * TimeUnit.MILLIS_PER_MINUTE);
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		throw new RuntimeException("operation not supported");
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
