package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;

public class EventFactionsInvitedChange extends EventFactionsAbstractSender
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
	
	private final MPlayer uplayer;
	public MPlayer getUPlayer() { return this.uplayer; }
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private boolean newInvited;
	public boolean isNewInvited() { return this.newInvited; }
	public void setNewInvited(boolean newInvited) { this.newInvited = newInvited; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsInvitedChange(CommandSender sender, MPlayer uplayer, Faction faction, boolean newInvited)
	{
		super(sender);
		this.uplayer = uplayer;
		this.faction = faction;
		this.newInvited = newInvited;
	}
	
}