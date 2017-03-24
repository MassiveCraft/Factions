package com.massivecraft.factions.event;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.event.EventMassiveCore;
import org.bukkit.command.CommandSender;

public abstract class EventFactionsAbstractSender extends EventMassiveCore
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private final CommandSender sender;
	public CommandSender getSender() { return this.sender; }
	public MPlayer getMPlayer() { return this.sender == null ? null : MPlayer.get(this.sender); }
	
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
