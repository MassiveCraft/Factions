package com.massivecraft.factions.engine;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.massivecraft.factions.event.EventFactionsPowerChange.PowerChangeReason;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.PlayerUtil;

public class EnginePower extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EnginePower i = new EnginePower();
	public static EnginePower get() { return i; }

	// -------------------------------------------- //
	// POWER LOSS ON DEATH
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL)
	public void powerLossOnDeath(PlayerDeathEvent event)
	{
		// If a player dies ...
		Player player = event.getEntity();
		if (MUtil.isntPlayer(player)) return;

		// ... and this is the first death event this tick ...
		// (yeah other plugins can case death event to fire twice the same tick)
		if (PlayerUtil.isDuplicateDeathEvent(event)) return;

		MPlayer mplayer = MPlayer.get(player);

		// ... and powerloss can happen here ...
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));

		if (!faction.getFlag(MFlag.getFlagPowerloss()))
		{
			mplayer.msg("<i>You didn't lose any power since the territory you died in works that way.");
			return;
		}

		if (!MConf.get().worldsPowerLossEnabled.contains(player.getWorld()))
		{
			mplayer.msg("<i>You didn't lose any power due to the world you died in.");
			return;
		}

		// ... alter the power ...
		double newPower = mplayer.getPower() + mplayer.getPowerPerDeath();

		EventFactionsPowerChange powerChangeEvent = new EventFactionsPowerChange(null, mplayer, PowerChangeReason.DEATH, newPower);
		powerChangeEvent.run();
		if (powerChangeEvent.isCancelled()) return;
		newPower = powerChangeEvent.getNewPower();

		mplayer.setPower(newPower);

		// ... and inform the player.
		// TODO: A progress bar here would be epic :)
		mplayer.msg("<i>Your power is now <h>%.2f / %.2f", newPower, mplayer.getPowerMax());
	}

}
