package com.massivecraft.factions.event;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

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
	
	private final MPlayer mplayer;
	public MPlayer getMPlayer() { return this.mplayer; }
	
	private final Faction faction;
	public Faction getFaction() { return this.faction; }
	
	private boolean newInvited;
	public boolean isNewInvited() { return this.newInvited; }
	public void setNewInvited(boolean newInvited) { this.newInvited = newInvited; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsInvitedChange(CommandSender sender, MPlayer mplayer, Faction faction, boolean newInvited)
	{
		super(sender);
		this.mplayer = mplayer;
		this.faction = faction;
		this.newInvited = newInvited;
	}
	
}
