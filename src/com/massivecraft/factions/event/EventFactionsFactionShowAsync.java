package com.massivecraft.factions.event;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.massivecore.PriorityLines;

public class EventFactionsFactionShowAsync extends EventFactionsAbstractSender
{
	// -------------------------------------------- //
	// REQUIRED EVENT CODE
	// -------------------------------------------- //
	
	private static final HandlerList handlers = new HandlerList();
	@Override public HandlerList getHandlers() { return handlers; }
	public static HandlerList getHandlerList() { return handlers; }
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private final Map<String, PriorityLines> idPriorityLiness;
	public Map<String, PriorityLines> getIdPriorityLiness() { return this.idPriorityLiness; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsFactionShowAsync(CommandSender sender, Faction faction)
	{
		super(true, sender);
		this.faction = faction;
		this.idPriorityLiness = new HashMap<String, PriorityLines>();
	}
	
}
