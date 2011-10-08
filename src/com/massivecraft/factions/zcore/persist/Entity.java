package com.massivecraft.factions.zcore.persist;

public abstract class Entity
{
	public Entity()
	{
	
	}
	
	protected transient String id = null;
	
	public String getId()
	{
		return id;
	}
	
	protected void setId(String id)
	{
		this.id = id;
	}
	
	public boolean shouldBeSaved()
	{
		return true;
	}
	
	// -------------------------------------------- //
	// ATTACH AND DETACH
	// -------------------------------------------- //
	
	public void attach()
	{
		EM.attach(this);
	}
	
	public void detach()
	{
		EM.detach(this);
	}
	
	public boolean attached()
	{
		return EM.attached(this);
	}
	
	public boolean detached()
	{
		return EM.detached(this);
	}
	
	// -------------------------------------------- //
	// EVENTS
	// -------------------------------------------- //
	
	public void preDetach()
	{
		
	}
	
	public void postDetach()
	{
		
	}
	
}
