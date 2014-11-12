package com.massivecraft.factions.event;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.massivecore.CaseInsensitiveComparator;
import com.massivecraft.massivecore.collections.MassiveTreeMap;

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
	
	private final MassiveTreeMap<String, Boolean, CaseInsensitiveComparator> expansions = new MassiveTreeMap<String, Boolean, CaseInsensitiveComparator>(CaseInsensitiveComparator.get());
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
