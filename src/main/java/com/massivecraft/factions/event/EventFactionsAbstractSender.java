package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.massivecore.event.EventMassiveCore;

public abstract class EventFactionsAbstractSender extends EventMassiveCore
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final CommandSender sender;
	public CommandSender getSender() { return this.sender; }
	public UPlayer getUSender() { return this.sender == null ? null : UPlayer.get(this.sender); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsAbstractSender(CommandSender sender)
	{
		this.sender = sender;
	}
}
