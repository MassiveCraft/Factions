package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.Rank;
import com.massivecraft.factions.entity.MPlayer;

public class EventFactionsRankChange extends EventFactionsAbstractSender
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
	
	private Rank newRank;
	public Rank getNewRank() { return this.newRank; }
	public void setNewRank(Rank newRole) { this.newRank = newRole; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsRankChange(CommandSender sender, MPlayer mplayer, Rank newRank)
	{
		super(sender);
		this.mplayer = mplayer;
		this.newRank = newRank;
	}
}
