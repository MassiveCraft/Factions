package com.massivecraft.factions.chat.modifier;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatModifierAbstract;

public class ChatModifierLc extends ChatModifierAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatModifierLc() { super("lc"); }
	private static ChatModifierLc i = new ChatModifierLc();
	public static ChatModifierLc get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getModified(String subject, CommandSender sender, CommandSender recipient)
	{
		return subject.toLowerCase();
	}

}
