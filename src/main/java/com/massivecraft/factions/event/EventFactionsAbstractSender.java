package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.event.EventMassiveCore;

public abstract class EventFactionsAbstractSender extends EventMassiveCore
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final CommandSender sender;
	public CommandSender getSender() { return this.sender; }
	public MPlayer getMSender() { return this.sender == null ? null : MPlayer.get(this.sender); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsAbstractSender(CommandSender sender)
	{
		this.sender = sender;
	}
	
	public EventFactionsAbstractSender(boolean async, CommandSender sender)
	{
		super(async);
		this.sender = sender;
	}
}
