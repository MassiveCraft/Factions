package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.TimeUnit;

public class TaskPlayerDataRemove extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static TaskPlayerDataRemove i = new TaskPlayerDataRemove();
	public static TaskPlayerDataRemove get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public long getDelayMillis()
	{
		// The interval is determined by the MConf rather than being set with setDelayMillis.
		return (long) (MConf.get().taskPlayerDataRemoveMinutes * TimeUnit.MILLIS_PER_MINUTE);
	}
	
	@Override
	public void invoke(long now)
	{
		// If this is the task server ...
		if (!MassiveCore.isTaskServer()) return;
		
		// ... check players for expiration.
		MPlayerColl.get().considerRemovePlayerMillis();
	}

}
