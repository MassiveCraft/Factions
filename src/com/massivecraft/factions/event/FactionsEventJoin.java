package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;

public class FactionsEventJoin extends FactionsEventAbstractSender
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
	
	private final PlayerJoinReason reason;
	public PlayerJoinReason getReason() { return reason; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsEventJoin(CommandSender sender, FPlayer fplayer, Faction faction, PlayerJoinReason reason)
	{
		super(sender);
		this.fplayer = fplayer;
		this.faction = faction;
		this.reason = reason;
	}
	
	// -------------------------------------------- //
	// INTERNAL ENUM
	// -------------------------------------------- //
	
	public enum PlayerJoinReason
	{
		CREATE, LEADER, JOIN
	}
	
}