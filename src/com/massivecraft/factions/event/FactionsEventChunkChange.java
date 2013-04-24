package com.massivecraft.factions.event;

import org.bukkit.command.CommandSender;
import org.bukkit.event.HandlerList;

import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.ps.PS;

public class FactionsEventChunkChange extends FactionsEventAbstractSender
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
	
	private final PS chunk;
	public PS getChunk() { return this.chunk; }
	
	private final Faction newFaction;
	public Faction getNewFaction() { return this.newFaction; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsEventChunkChange(CommandSender sender, PS chunk, Faction newFaction)
	{
		super(sender);
		this.chunk = chunk.getChunk(true);
		this.newFaction = newFaction;
	}
	
	// -------------------------------------------- //
	// UTIL
	// -------------------------------------------- //
	
	public FactionsEventChunkChangeType getType()
	{
		Faction currentFaction = BoardColls.get().getFactionAt(chunk);
		
		if (currentFaction.isNone()) return FactionsEventChunkChangeType.BUY;
		if (newFaction.isNormal()) return FactionsEventChunkChangeType.CONQUER;
		
		UPlayer usender = this.getUSender();
		if (usender != null && usender.getFaction() == currentFaction) return FactionsEventChunkChangeType.SELL;
		
		return FactionsEventChunkChangeType.PILLAGE;
	}
	
}
