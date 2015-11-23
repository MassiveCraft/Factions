package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPerm;

public class EventFactionsPermChange extends EventFactionsAbstractSender
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
	
	private final MPerm perm;
	public MPerm getPerm() { return this.perm; }
	
	private final Rel rel;
	public Rel getRel() { return this.rel; }
	
	private boolean newValue;
	public boolean getNewValue() { return this.newValue; }
	public void setNewValue(boolean newValue) { this.newValue = newValue; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsPermChange(CommandSender sender, Faction faction, MPerm perm, Rel rel, boolean newValue)
	{
		super(sender);
		this.faction = faction;
		this.perm = perm;
		this.rel = rel;
		this.newValue = newValue;
	}
	
}
