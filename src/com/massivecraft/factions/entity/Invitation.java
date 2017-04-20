package com.massivecraft.factions.entity;

import com.massivecraft.massivecore.store.EntityInternal;

public class Invitation extends EntityInternal<Invitation>
{
	// -------------------------------------------- //
	// OVERRIDE: ENTITY
	// -------------------------------------------- //
	
	@Override
	public Invitation load(Invitation that)
	{
		this.inviterId = that.inviterId;
		this.creationMillis = that.creationMillis;
		
		return this;
	}
	
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	private String inviterId;
	public String getInviterId() { return inviterId; }
	public void setInviterId(String inviterId) { this.inviterId = inviterId; }
	
	private Long creationMillis;
	public Long getCreationMillis() { return creationMillis; }
	public void setCreationMillis(Long creationMillis) { this.creationMillis = creationMillis; }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public Invitation()
	{
		this(null, null);
	}
	
	public Invitation(String inviterId, Long creationMillis)
	{
		this.inviterId = inviterId;
		this.creationMillis = creationMillis;
	}
	
}
