package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.massivecore.MassiveCore;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.TimeUnit;

public class TaskEconLandReward extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static TaskEconLandReward i = new TaskEconLandReward();
	public static TaskEconLandReward get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public long getDelayMillis()
	{
		// The interval is determined by the MConf rather than being set with setDelayMillis.
		return (long) (MConf.get().taskEconLandRewardMinutes * TimeUnit.MILLIS_PER_MINUTE);
	}
	
	@Override
	public void invoke(long now)
	{
		// If this is the task server ...
		if (!MassiveCore.isTaskServer()) return;
		
		// ... process the econ land rewards.
		FactionColl.get().econLandRewardRoutine();
	}
	
}
