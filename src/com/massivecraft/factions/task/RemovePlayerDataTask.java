package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UPlayerColl;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.mcore.ModuloRepeatTask;

public class RemovePlayerDataTask extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static RemovePlayerDataTask i = new RemovePlayerDataTask();
	public static RemovePlayerDataTask get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: MODULO REPEAT TASK
	// -------------------------------------------- //
	
	@Override
	public long getDelayMillis()
	{
		return MConf.get().taskAutoLeaveMillis;
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		MConf.get().taskAutoLeaveMillis = delayMillis;
	}
	
	@Override
	public void invoke()
	{
		for (UPlayerColl coll : UPlayerColls.get().getColls())
		{
			coll.removePlayerDataAfterInactiveDaysRoutine();
		}
	}
	
}
