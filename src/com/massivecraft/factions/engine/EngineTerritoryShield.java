package com.massivecraft.factions.engine;

import java.text.MessageFormat;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.util.MUtil;

public class EngineTerritoryShield extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineTerritoryShield i = new EngineTerritoryShield();
	public static EngineTerritoryShield get() { return i; }

	// -------------------------------------------- //
	// TERRITORY SHIELD
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void territoryShield(EntityDamageByEntityEvent event)
	{
		// If the entity is a player ...
		Entity entity = event.getEntity();
		if (MUtil.isntPlayer(entity)) return;
		Player player = (Player)entity;
		MPlayer mplayer = MPlayer.get(player);
		
		// ... and the attacker is a player ...
		Entity attacker = MUtil.getLiableDamager(event);
		if (! (attacker instanceof Player)) return;
		
		// ... and that player has a faction ...
		if ( ! mplayer.hasFaction()) return;
		
		// ... and that player is in their own territory ...
		if ( ! mplayer.isInOwnTerritory()) return;
		
		// ... and a territoryShieldFactor is configured ...
		if (MConf.get().territoryShieldFactor <= 0) return;
		
		// ... then scale the damage ...
		double factor = 1D - MConf.get().territoryShieldFactor;
		MUtil.scaleDamage(event, factor);
		
		// ... and inform.
		String perc = MessageFormat.format("{0,number,#%}", (MConf.get().territoryShieldFactor));
		mplayer.msg("<i>Enemy damage reduced by <rose>%s<i>.", perc);
	}

}
