package com.massivecraft.factions.event;

import com.massivecraft.massivecore.collections.MassiveTreeMap;
import com.massivecraft.massivecore.comparator.ComparatorCaseInsensitive;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import java.util.Map;

public class EventFactionsExpansions extends EventFactionsAbstractSender
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
	
	private final MassiveTreeMap<String, Boolean, ComparatorCaseInsensitive> expansions = new MassiveTreeMap<>(ComparatorCaseInsensitive.get());
	public Map<String, Boolean> getExpansions() { return this.expansions; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsExpansions(CommandSender sender)
	{
		super(sender);
		this.getExpansions().put("FactionsTax", false);
		this.getExpansions().put("FactionsDynmap", false);
	}
	
}
