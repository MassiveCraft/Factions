package com.massivecraft.factions.task;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.massivecraft.factions.event.EventFactionsPowerChange.PowerChangeReason;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.TimeUnit;

public class TaskPlayerPowerUpdate extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TaskPlayerPowerUpdate i = new TaskPlayerPowerUpdate();
	public static TaskPlayerPowerUpdate get() { return i; }
	
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
		return (long) (MConf.get().taskPlayerPowerUpdateMinutes * TimeUnit.MILLIS_PER_MINUTE);
	}
	
	@Override
	public void setDelayMillis(long delayMillis)
	{
		MConf.get().taskPlayerPowerUpdateMinutes = delayMillis / (double) TimeUnit.MILLIS_PER_MINUTE;
	}
	
	@Override
	public void invoke(long now)
	{
		long millis = this.getDelayMillis();
		
		for (Player player : MUtil.getOnlinePlayers())
		{
			if (MUtil.isntPlayer(player)) continue;
			if (player.isDead()) continue;
			
			MPlayer mplayer = MPlayer.get(player);
			double newPower = mplayer.getPower() + mplayer.getPowerPerHour() * millis / TimeUnit.MILLIS_PER_HOUR;
			
			EventFactionsPowerChange event = new EventFactionsPowerChange(null, mplayer, PowerChangeReason.TIME, newPower);
			event.run();
			if (event.isCancelled()) continue;
			newPower = event.getNewPower();
			
			mplayer.setPower(newPower);
		}
	}
	
}
