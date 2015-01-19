package com.massivecraft.factions.task;

import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.TimeUnit;

public class TaskFlagPermCreate extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TaskFlagPermCreate i = new TaskFlagPermCreate();
	public static TaskFlagPermCreate get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: MODULO REPEAT TASK
	// -------------------------------------------- //
	
	@Override
	public Plugin getPlugin()
	{
		return Factions.get();
	}
	
	@Override
	public long getDelayMillis()
	{
		return TimeUnit.MILLIS_PER_SECOND * 3;
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		
	}
	
	@Override
	public void invoke(long now)
	{
		MPerm.getAll();
		MFlag.getAll();
	}
	
}
