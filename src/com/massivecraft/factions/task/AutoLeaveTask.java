package com.massivecraft.factions.task;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.entity.UPlayerColl;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.mcore.ModuloRepeatTask;
import com.massivecraft.mcore.util.TimeUnit;

public class AutoLeaveTask extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static AutoLeaveTask i = new AutoLeaveTask();
	public static AutoLeaveTask get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: MODULO REPEAT TASK
	// -------------------------------------------- //
	
	@Override
	public long getDelayMillis()
	{
		return (long) (ConfServer.autoLeaveRoutineRunsEveryXMinutes * TimeUnit.MILLIS_PER_MINUTE);
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		throw new RuntimeException("operation not supported");
	}
	
	@Override
	public void invoke()
	{
		for (UPlayerColl coll : UPlayerColls.get().getColls())
		{
			coll.autoLeaveOnInactivityRoutine();
		}
	}
	
}
