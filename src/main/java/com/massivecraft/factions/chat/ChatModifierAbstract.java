package com.massivecraft.factions.chat;

public abstract class ChatModifierAbstract implements ChatModifier
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
		return ChatFormatter.registerModifier(this);
	}
	
	@Override
	public boolean unregister()
	{
		return ChatFormatter.unregisterModifier(this);
	}
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public ChatModifierAbstract(final String id)
	{
		this.id = id.toLowerCase();
	}
	
}
