package com.massivecraft.factions.chat.tag;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.MPlayer;

public class ChatTagTitle extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagTitle() { super("factions_title"); }
	private static ChatTagTitle i = new ChatTagTitle();
	public static ChatTagTitle get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(CommandSender sender, CommandSender recipient)
	{
		// Get entities
		MPlayer usender = MPlayer.get(sender);
		
		if (!usender.hasTitle()) return "";
		return usender.getTitle();
	}

}
