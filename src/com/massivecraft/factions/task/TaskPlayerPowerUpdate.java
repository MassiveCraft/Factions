package com.massivecraft.factions.task;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.massivecraft.factions.event.EventFactionsPowerChange.PowerChangeReason;
import com.massivecraft.massivecore.ModuloRepeatTask;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import org.bukkit.entity.Player;

public class TaskPlayerPowerUpdate extends ModuloRepeatTask
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static TaskPlayerPowerUpdate i = new TaskPlayerPowerUpdate();
	public static TaskPlayerPowerUpdate get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public long getDelayMillis()
	{
		// The interval is determined by the MConf rather than being set with setDelayMillis.
		return (long) (MConf.get().taskPlayerPowerUpdateMinutes * TimeUnit.MILLIS_PER_MINUTE);
	}
	
	@Override
	public void invoke(long now)
	{
		long millis = this.getDelayMillis();
		MFlag flagPowerGain = MFlag.getFlagPowergain();
		
		// For each player ...
		for (Player player : MUtil.getOnlinePlayers())
		{
			// ... that is a living player ...
			if (MUtil.isntPlayer(player)) continue;
			if (player.isDead()) continue;
			
			// ... in a faction territory that permits power gain ...
			Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player));
			if (!faction.getFlag(flagPowerGain)) return;
			
			// ... in a world that permits power gain ...
			if (!MConf.get().worldsPowerGainEnabled.contains(player)) return;
			
			MPlayer mplayer = MPlayer.get(player);
			
			// ... calculate new power ...
			double newPower = mplayer.getPower() + mplayer.getPowerPerHour() * millis / TimeUnit.MILLIS_PER_HOUR;
			
			// ... and if other plugins don't object ...
			EventFactionsPowerChange event = new EventFactionsPowerChange(null, mplayer, PowerChangeReason.TIME, newPower);
			event.run();
			if (event.isCancelled()) continue;
			
			// ... set the new power for the player.
			newPower = event.getNewPower();
			mplayer.setPower(newPower);
		}
	}

}
