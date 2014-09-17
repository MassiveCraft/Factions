package com.massivecraft.factions.chat.tag;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.util.Txt;

public class ChatTagRole extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagRole() { super("factions_role"); }
	private static ChatTagRole i = new ChatTagRole();
	public static ChatTagRole get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(CommandSender sender, CommandSender recipient)
	{
		// Get entities
		MPlayer usender = MPlayer.get(sender);
		
		return Txt.upperCaseFirst(usender.getRole().toString().toLowerCase());
	}

}
