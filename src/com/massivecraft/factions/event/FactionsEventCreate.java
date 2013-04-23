package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class FactionsEventCreate extends FactionsEventAbstractSender
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

	private final String universe;
	public final String getUniverse() { return this.universe; }

	private final String factionId;
	public final String getFactionId() { return this.factionId; }

	private final String factionTag;
	public final String getFactionTag() { return this.factionTag; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public FactionsEventCreate(CommandSender sender, String universe, String factionId, String factionTag)
	{
		super(sender);
		this.universe = universe;
		this.factionId = factionId;
		this.factionTag = factionTag;
	}

}
