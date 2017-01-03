package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.TimeUnit;

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
		if ( ! MassiveCore.isTaskServer()) return;
		MPlayerColl.get().considerRemovePlayerMillis();
	}

}
