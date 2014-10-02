package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MFlag;

public class EventFactionsFlagChange extends EventFactionsAbstractSender
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
	
	private final MFlag flag;
	public MFlag getFlag() { return this.flag; }
	
	private boolean newValue;
	public boolean isNewValue() { return this.newValue; }
	public void setNewValue(boolean newValue) { this.newValue = newValue; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsFlagChange(CommandSender sender, Faction faction, MFlag flag, boolean newValue)
	{
		super(sender);
		this.faction = faction;
		this.flag = flag;
		this.newValue = newValue;
	}
	
}
