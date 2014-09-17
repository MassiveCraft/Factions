package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.MPlayer;

public class EventFactionsTitleChange extends EventFactionsAbstractSender
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
	
	private final MPlayer uplayer;
	public MPlayer getUPlayer() { return this.uplayer; }
	
	private String newTitle;
	public String getNewTitle() { return this.newTitle; }
	public void setNewTitle(String newTitle) { this.newTitle = newTitle; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public EventFactionsTitleChange(CommandSender sender, MPlayer uplayer, String newTitle)
	{
		super(sender);
		this.uplayer = uplayer;
		this.newTitle = newTitle;
	}
	
}
