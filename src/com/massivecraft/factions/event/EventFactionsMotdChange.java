package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.Faction;

public class EventFactionsMotdChange extends EventFactionsAbstractSender
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
	
	private String newMotd;
	public String getNewMotd() { return this.newMotd; }
	public void setNewMotd(String newMotd) { this.newMotd = newMotd; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsMotdChange(CommandSender sender, Faction faction, String newMotd)
	{
		super(sender);
		this.faction = faction;
		this.newMotd = newMotd;
	}
	
}
