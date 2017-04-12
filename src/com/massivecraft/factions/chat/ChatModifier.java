package com.massivecraft.factions.chat;

import com.massivecraft.massivecore.collections.MassiveMap;
import org.bukkit.command.CommandSender;

import java.util.Map;

public abstract class ChatModifier extends ChatActive
{
	// -------------------------------------------- //
	// MODIFIER REGISTER
	// -------------------------------------------- //
	
	private final static Map<String, ChatModifier> idToModifier = new MassiveMap<>();
	public static ChatModifier getModifier(String modifierId) { return idToModifier.get(modifierId); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public ChatModifier(final String id)
	{
		super(id);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean isActive()
	{
		return idToModifier.containsKey(this.getId());
	}
	
	@Override
	public void setActive(boolean active)
	{
		if (active)
		{
			idToModifier.put(this.getId(), this);
		}
		else
		{
			idToModifier.remove(this.getId());
		}
	}
	
	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	
	public abstract String getModified(String subject, CommandSender sender, CommandSender recipient);
}
