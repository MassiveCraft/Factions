package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FactionsEventMembershipChange extends FactionsEventAbstractSender
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
	
	@Override
	public void setCancelled(boolean cancelled) 
	{
		if (!this.reason.isCancellable()) cancelled = false;
		super.setCancelled(cancelled);		
	}
	
	private final FPlayer fplayer;
	public FPlayer getFPlayer() { return this.fplayer; }
	
	private final Faction newFaction;
	public Faction getNewFaction() { return this.newFaction; }
	
	private final MembershipChangeReason reason;
	public MembershipChangeReason getReason() { return this.reason; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsEventMembershipChange(CommandSender sender, FPlayer fplayer, Faction newFaction, MembershipChangeReason reason)
	{
		super(sender);
		this.fplayer = fplayer;
		this.newFaction = newFaction;
		this.reason = reason;
	}
	
	// -------------------------------------------- //
	// INTERNAL ENUM
	// -------------------------------------------- //
	
	public enum MembershipChangeReason
	{
		// Join
		JOIN      (true),
		CREATE    (false),
		LEADER    (true),
		
		// Leave
		LEAVE     (true),
		//JOINOTHER (true),
		KICK      (true),
		DISBAND   (false),
		//RESET     (false),
		;
		
		private final boolean cancellable;
		public boolean isCancellable() { return this.cancellable; }
		
		private MembershipChangeReason(boolean cancellable)
		{
			this.cancellable = cancellable;
		}
	}
	
}