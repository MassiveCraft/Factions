package com.massivecraft.factions.task;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.event.FactionsEventPowerChange;
import com.massivecraft.factions.event.FactionsEventPowerChange.PowerChangeReason;
import com.massivecraft.mcore.ModuloRepeatTask;
import com.massivecraft.mcore.util.TimeUnit;

public class PowerUpdateTask extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static PowerUpdateTask i = new PowerUpdateTask();
	public static PowerUpdateTask get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE: MODULO REPEAT TASK
	// -------------------------------------------- //
	
	@Override
	public long getDelayMillis()
	{
		return MConf.get().powerTaskMillis;
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		MConf.get().powerTaskMillis = delayMillis;
	}
	
	@Override
	public void invoke()
	{
		long millis = this.getDelayMillis();
		
		for (Player player : Bukkit.getOnlinePlayers())
		{
			if (player.isDead()) continue;
			
			UPlayer uplayer = UPlayer.get(player);
			double newPower = uplayer.getPower() + uplayer.getPowerPerHour() * millis / TimeUnit.MILLIS_PER_HOUR;
			
			FactionsEventPowerChange event = new FactionsEventPowerChange(null, uplayer, PowerChangeReason.TIME, newPower);
			event.run();
			if (event.isCancelled()) continue;
			newPower = event.getNewPower();
			
			uplayer.setPower(newPower);
		}
	}
	
}
