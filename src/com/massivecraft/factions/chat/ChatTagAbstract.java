package com.massivecraft.factions.chat;

public abstract class ChatTagAbstract implements ChatTag
{
	// -------------------------------------------- //
	// FIELDS & RAWDATA GET/SET
	// -------------------------------------------- //
	
	private final String id;
	@Override public String getId() { return this.id; }

	// -------------------------------------------- //
	// OVERRIDES
	// -------------------------------------------- //

	@Override
	public boolean register()
	{
		return ChatFormatter.registerTag(this);
	}
	
	@Override
	public boolean unregister()
	{
		return ChatFormatter.unregisterTag(this);
	}
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public ChatTagAbstract(final String id)
	{
		this.id = id.toLowerCase();
	}
	
}
