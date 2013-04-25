package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayerColl;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.mcore.ModuloRepeatTask;
import com.massivecraft.mcore.util.TimeUnit;

public class TaskPlayerDataRemove extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TaskPlayerDataRemove i = new TaskPlayerDataRemove();
	public static TaskPlayerDataRemove get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: MODULO REPEAT TASK
	// -------------------------------------------- //
	
	@Override
	public long getDelayMillis()
	{
		return (long) (MConf.get().taskPlayerDataRemoveMinutes * TimeUnit.MILLIS_PER_MINUTE);
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		MConf.get().taskPlayerDataRemoveMinutes = delayMillis / (double) TimeUnit.MILLIS_PER_MINUTE;
	}
	
	@Override
	public void invoke(long now)
	{
		for (UPlayerColl coll : UPlayerColls.get().getColls())
		{
			// Check disabled
			if (UConf.isDisabled(coll)) continue;
			
			coll.removePlayerDataAfterInactiveDaysRoutine();
		}
	}
	
}
