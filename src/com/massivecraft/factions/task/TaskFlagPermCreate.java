package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.TimeUnit;

public class TaskFlagPermCreate extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	private static final long MILLIS_INTERVAL = TimeUnit.MILLIS_PER_SECOND * 3;
	
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TaskFlagPermCreate i = new TaskFlagPermCreate();
	public static TaskFlagPermCreate get() { return i; }
	
	public TaskFlagPermCreate()
	{
		super(MILLIS_INTERVAL);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		// No operation
	}
	
	@Override
	public void invoke(long now)
	{
		MPerm.getAll();
		MFlag.getAll();
	}
	
}
