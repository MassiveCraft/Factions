package com.massivecraft.factions.event;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.Colorized;
import org.bukkit.ChatColor;

public enum EventFactionsChunkChangeType implements Colorized
{
	// -------------------------------------------- //
	// ENUM
	// -------------------------------------------- //
	
	NONE("none", "none", ChatColor.WHITE),
	BUY("buy", "bought", ChatColor.GREEN),
	SELL("sell", "sold", ChatColor.GREEN),
	CONQUER("conquer", "conquered", ChatColor.DARK_GREEN),
	PILLAGE("pillage", "pillaged", ChatColor.RED),
	
	// END OF LIST
	;
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public final String now;
	public final String past;
	
	public final ChatColor color;
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	EventFactionsChunkChangeType(String now, String past, ChatColor color)
	{
		this.now = now;
		this.past = past;
		this.color = color;
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public ChatColor getColor()
	{
		return this.color;
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public static EventFactionsChunkChangeType get(Faction oldFaction, Faction newFaction, Faction self)
	{
		if (newFaction == oldFaction) return NONE;
		if (oldFaction.isNone()) return BUY;
		if (newFaction.isNormal()) return CONQUER;
		if (oldFaction == self) return SELL;
		return PILLAGE;
	}
	
}
