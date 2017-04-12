package com.massivecraft.factions.event;

import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MPlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

public class EventFactionsMembershipChange extends EventFactionsAbstractSender
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
	
	private final MPlayer mplayer;
	public MPlayer getMPlayer() { return this.mplayer; }
	
	private final Faction newFaction;
	public Faction getNewFaction() { return this.newFaction; }
	
	private final MembershipChangeReason reason;
	public MembershipChangeReason getReason() { return this.reason; }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsMembershipChange(CommandSender sender, MPlayer mplayer, Faction newFaction, MembershipChangeReason reason)
	{
		super(sender);
		this.mplayer = mplayer;
		this.newFaction = newFaction;
		this.reason = reason;
	}
	
	// -------------------------------------------- //
	// REASON ENUM
	// -------------------------------------------- //
	
	public enum MembershipChangeReason
	{
		// Join
		JOIN (true),
		CREATE (false),
		// Leader is not used, but temporarily kept to avoid other plugins crashing
		@Deprecated
		LEADER (true),
		RANK (true),
		
		// Leave
		LEAVE (true),
		//JOINOTHER (true),
		KICK (true),
		DISBAND (false),
		//RESET	 (false),
		;
		
		private final boolean cancellable;
		public boolean isCancellable() { return this.cancellable; }
		
		MembershipChangeReason(boolean cancellable)
		{
			this.cancellable = cancellable;
		}
	}
	
}
