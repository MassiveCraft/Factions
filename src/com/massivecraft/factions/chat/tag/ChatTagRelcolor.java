package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTag;
import com.massivecraft.factions.entity.MPlayer;
import org.bukkit.command.CommandSender;

public class ChatTagRelcolor extends ChatTag
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagRelcolor() { super("factions_relcolor"); }
	private static ChatTagRelcolor i = new ChatTagRelcolor();
	public static ChatTagRelcolor get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(CommandSender sender, CommandSender recipient)
	{
		// Opt out if no recipient
		if (recipient == null) return null;

		// Get entities
		MPlayer usender = MPlayer.get(sender);
		MPlayer urecipient = MPlayer.get(recipient);
		
		return urecipient.getRelationTo(usender).getColor().toString();
	}

}
