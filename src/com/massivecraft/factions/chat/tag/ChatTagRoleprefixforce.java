package com.massivecraft.factions.chat.tag;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;

public class ChatTagRoleprefixforce extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagRoleprefixforce() { super("factions_roleprefix"); }
	private static ChatTagRoleprefixforce i = new ChatTagRoleprefixforce();
	public static ChatTagRoleprefixforce get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(CommandSender sender, CommandSender recipient)
	{
		// Check disabled
		if (UConf.isDisabled(sender)) return "";
		
		// Get entities
		UPlayer usender = UPlayer.get(sender);
		
		return usender.getRole().getPrefix();
	}

}
