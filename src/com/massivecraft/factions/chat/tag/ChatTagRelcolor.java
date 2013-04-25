package com.massivecraft.factions.chat.tag;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;

public class ChatTagRelcolor extends ChatTagAbstract
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
		if (recipient == null) return "";
		
		// Check disabled
		if (UConf.isDisabled(sender)) return "";

		// Get entities
		UPlayer usender = UPlayer.get(sender);
		UPlayer urecipient = UPlayer.get(recipient);
		
		return urecipient.getRelationTo(usender).getColor().toString();
	}

}
