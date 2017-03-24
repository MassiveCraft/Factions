package com.massivecraft.factions.event;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.event.EventMassiveCore;
import org.bukkit.event.HandlerList;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class EventFactionsRemovePlayerMillis extends EventMassiveCore
{
	// -------------------------------------------- //
	// REQUIRED EVENT CODE
	// -------------------------------------------- //
	
	private static final HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
	// -------------------------------------------- //
	// FIELD
	// -------------------------------------------- //
	
	private final MPlayer mplayer;
	public MPlayer getMPlayer() { return this.mplayer; }
	
	private long millis;
	public long getMillis() { return this.millis; }
	public void setMillis(long millis) { this.millis = millis; }
	
	private Map<String, Long> causeMillis = new LinkedHashMap<>();
	public Map<String, Long> getCauseMillis() { return this.causeMillis; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsRemovePlayerMillis(boolean async, MPlayer mplayer)
	{
		super(async);
		
		this.mplayer = mplayer;
		this.millis = MConf.get().removePlayerMillisDefault;
		
		// Default
		this.causeMillis.put("Default", MConf.get().removePlayerMillisDefault);
		
		// Player Age Bonus
		this.applyPlayerAgeBonus();
		
		// Faction Age Bonus
		this.applyFactionAgeBonus();
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public void applyPlayerAgeBonus()
	{
		// Skip if this bonus is totally disabled.
		// We don't want it showing up with 0 for everyone.
		if (MConf.get().removePlayerMillisPlayerAgeToBonus.isEmpty()) return;
		
		// Calculate First Played
		Long firstPlayed = this.getMPlayer().getFirstPlayed();
		Long age = 0L;
		if (firstPlayed != null)
		{
			age = System.currentTimeMillis() - firstPlayed;
		}
		
		// Calculate the Bonus!
		long bonus = 0;
		for (Entry<Long, Long> entry : MConf.get().removePlayerMillisPlayerAgeToBonus.entrySet())
		{
			Long key = entry.getKey();
			if (key == null) continue;
			
			Long value = entry.getValue();
			if (value == null) continue;
			
			if (age >= key)
			{
				bonus = value;
				break;
			}
		}
		
		// Apply
		this.setMillis(this.getMillis() + bonus);
		
		// Inform
		this.getCauseMillis().put("Player Age Bonus", bonus);
	}
	
	public void applyFactionAgeBonus()
	{
		// Skip if this bonus is totally disabled.
		// We don't want it showing up with 0 for everyone.
		if (MConf.get().removePlayerMillisFactionAgeToBonus.isEmpty()) return;
		
		// Calculate Faction Age
		Faction faction = this.getMPlayer().getFaction();
		long age = 0;
		if ( ! faction.isNone())
		{
			age = System.currentTimeMillis() - faction.getCreatedAtMillis();
		}
		
		// Calculate the Bonus!
		long bonus = 0;
		for (Entry<Long, Long> entry : MConf.get().removePlayerMillisFactionAgeToBonus.entrySet())
		{
			Long key = entry.getKey();
			if (key == null) continue;
			
			Long value = entry.getValue();
			if (value == null) continue;
			
			if (age >= key)
			{
				bonus = value;
				break;
			}
		}
		
		// Apply
		this.setMillis(this.getMillis() + bonus);
		
		// Inform
		this.getCauseMillis().put("Faction Age Bonus", bonus);
	}
	
}
