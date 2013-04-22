package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.mcore.event.MCoreEvent;

public abstract class FactionsEventAbstractSender extends MCoreEvent
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final CommandSender sender;
	public CommandSender getSender() { return this.sender; }
	public FPlayer getFSender() { return FPlayer.get(this.sender); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsEventAbstractSender(CommandSender sender)
	{
		this.sender = sender;
	}
}
