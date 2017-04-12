package com.massivecraft.factions.chat;

import com.massivecraft.massivecore.collections.MassiveMap;
import org.bukkit.command.CommandSender;

import java.util.Map;

public abstract class ChatTag extends ChatActive
{
	// -------------------------------------------- //
	// TAG REGISTER
	// -------------------------------------------- //
	
	private final static Map<String, ChatTag> idToTag = new MassiveMap<>();
	public static ChatTag getTag(String tagId) { return (ChatTag) idToTag.get(tagId); }
	
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public ChatTag(final String id)
	{
		super(id);
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public boolean isActive()
	{
		return idToTag.containsKey(this.getId());
	}
	
	@Override
	public void setActive(boolean active)
	{
		if (active)
		{
			idToTag.put(this.getId(), this);
		}
		else
		{
			idToTag.remove(this.getId());
		}
	}
	
	// -------------------------------------------- //
	// ABSTRACT
	// -------------------------------------------- //
	
	public abstract String getReplacement(CommandSender sender, CommandSender recipient);
	
}
