package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.mcore.ps.PS;


public class TodoFactionsEntityListener implements Listener
{

	// -------------------------------------------- //
	// POWER LOSS ON DEATH
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void powerLossOnDeath(PlayerDeathEvent event)
	{
		// If a player dies ...
		Player player = event.getEntity();
		FPlayer fplayer = FPlayerColl.get().get(player);
		
		// ... TODO: Sending the message through the event is not the best way of doing it.
		// TODO: We should listen to our own events and send message from there if we cancel.
		// 
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player));

		PowerLossEvent powerLossEvent = new PowerLossEvent(faction, fplayer);
		
		// Check for no power loss conditions
		if ( ! faction.getFlag(FFlag.POWERLOSS))
		{
			powerLossEvent.setMessage("<i>You didn't lose any power since the territory you died in works that way.");
			powerLossEvent.setCancelled(true);
		}
		else if (ConfServer.worldsNoPowerLoss.contains(player.getWorld().getName()))
		{
			powerLossEvent.setMessage("<i>You didn't lose any power due to the world you died in.");
			powerLossEvent.setCancelled(true);
		}
		else
		{
			powerLossEvent.setMessage("<i>Your power is now <h>%d / %d");
		}

		// call Event
		Bukkit.getPluginManager().callEvent(powerLossEvent);

		// Call player onDeath if the event is not cancelled
		if ( ! powerLossEvent.isCancelled())
		{
			fplayer.setPower(fplayer.getPower() + ConfServer.powerPerDeath);
		}
		
		// Send the message from the powerLossEvent
		final String msg = powerLossEvent.getMessage();
		if (msg != null && !msg.isEmpty())
		{
			fplayer.msg(msg, fplayer.getPowerRounded(), fplayer.getPowerMaxRounded());
		}
	}
	
	

	
	
}
