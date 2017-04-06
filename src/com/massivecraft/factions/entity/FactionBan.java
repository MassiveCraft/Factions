package com.massivecraft.factions.entity;

import com.massivecraft.factions.Selector;
import com.massivecraft.factions.cmd.type.TypeMPlayer;
import com.massivecraft.factions.cmd.type.TypeSelector;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.store.EntityInternal;

public class FactionBan extends EntityInternal<FactionBan>
{
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public FactionBan load(FactionBan that)
	{
		this.selectorId = that.selectorId;
		this.executorId = that.executorId;
		this.reason = that.reason;
		
		return this;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// This is the id of the selector which is banned for that faction.
	private String selectorId = null;
	public String getSelectorId() { return this.selectorId; }
	public void setSelectorId(String selectorId) { this.selectorId = selectorId; }
	
	// The id of the mplayer who banned the selector.
	private String executorId = null;
	public String getExecutorId() { return this.executorId; }
	public void setExecutorId(String executorId) { this.executorId = executorId; }
	
	// The reason given upon banning the selector.
	private String reason = null;
	public String getReason() { return this.reason; }
	public void setReason(String reason) { this.reason = reason; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionBan()
	{
		this(null, null, null);
	}
	
	public FactionBan(Selector selector, MPlayer executor, String reason)
	{
		this.selector = selector;
		this.executor = executor;
		this.selectorId = selector == null ? null : selector.getId();
		this.executorId = executor == null ? null : executor.getId();
		this.reason = reason;
	}
	
	// -------------------------------------------- //
	// LAZY RESOLVING
	// -------------------------------------------- //
	
	private transient Selector selector = null;
	private transient MPlayer executor = null;
	
	public Selector getSelector()
	{
		// Already stored?
		if (this.selector != null) return this.selector;
		
		// Resolve
		this.selector = TypeSelector.get().readSafe(this.getSelectorId(), null);
		
		// Return
		return this.selector;
	}
	
	public MPlayer getExecutor()
	{
		// Already stored?
		if (this.executor != null) return this.executor;
		
		// Resolve
		try
		{
			this.executor = TypeMPlayer.get().read(this.getExecutorId());
		}
		catch (MassiveException e)
		{
			throw new IllegalStateException("Executor couldn't be resolved: " + this.getExecutorId(), e);
		}
		
		// Return
		return this.executor;
	}
	
}
