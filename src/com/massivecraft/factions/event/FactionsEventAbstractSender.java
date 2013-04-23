package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.event.MCoreEvent;

public abstract class FactionsEventAbstractSender extends MCoreEvent
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //

	private final CommandSender sender;
	public CommandSender getSender() { return this.sender; }
	public UPlayer getFSender() { return UPlayer.get(this.sender); }

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //

	public FactionsEventAbstractSender(CommandSender sender)
	{
		this.sender = sender;
	}
}
