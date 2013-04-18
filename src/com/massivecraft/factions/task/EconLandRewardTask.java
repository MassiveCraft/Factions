package com.massivecraft.factions.task;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FactionColl;
import com.massivecraft.mcore.ModuloRepeatTask;
import com.massivecraft.mcore.util.TimeUnit;

public class EconLandRewardTask extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EconLandRewardTask i = new EconLandRewardTask();
	public static EconLandRewardTask get() { return i; }
	
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
		FactionColl.get().econLandRewardRoutine();
	}
	
}
