package com.massivecraft.factions.event;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.massivecraft.factions.engine.DisallowCause;
import com.massivecraft.factions.entity.MPlayer;

/**
 * This event is fired when PVP is disallowed between players due to any rules in Factions.
 * Canceling this event allows the PVP in spite of this and stops text messages from being sent.
 * 
 * Note that the defender field always is set but the attacker can be null.
 * Some other plugins seem to fire EntityDamageByEntityEvent without an attacker.
 */
public class EventFactionsPvpDisallowed extends EventFactionsAbstract
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
	
	private final Player attacker;
	public Player getAttacker() { return this.attacker; }
	public MPlayer getMAttacker() { return this.attacker == null ? null : MPlayer.get(this.attacker); }
	
	private final Player defender;
	public Player getDefender() { return this.defender; }
	public MPlayer getMDefender() { return this.defender == null ? null : MPlayer.get(this.defender); }
	
	private final DisallowCause cause;
	public DisallowCause getCause() { return this.cause; }
	
	private final EntityDamageByEntityEvent event;
	public EntityDamageByEntityEvent getEvent() { return this.event; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsPvpDisallowed(Player attacker, Player defender, DisallowCause cause, EntityDamageByEntityEvent event)
	{
		this.attacker = attacker;
		this.defender = defender;
		this.cause = cause;
		this.event = event;
	}

}
