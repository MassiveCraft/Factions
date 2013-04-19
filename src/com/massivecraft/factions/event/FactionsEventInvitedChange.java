package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FactionsEventInvitedChange extends FactionsEventAbstractSender
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
	
	private final FPlayer fplayer;
	public FPlayer getFPlayer() { return this.fplayer; }
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private boolean newInvited;
	public boolean isNewInvited() { return this.newInvited; }
	public void setNewInvited(boolean newInvited) { this.newInvited = newInvited; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsEventInvitedChange(CommandSender sender, FPlayer fplayer, Faction faction, boolean newInvited)
	{
		super(sender);
		this.fplayer = fplayer;
		this.faction = faction;
		this.newInvited = newInvited;
	}
	
}