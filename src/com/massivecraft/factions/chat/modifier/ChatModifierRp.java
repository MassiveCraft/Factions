package com.massivecraft.factions.chat.modifier;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatModifier;

public class ChatModifierRp extends ChatModifier
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatModifierRp() { super("rp"); }
	private static ChatModifierRp i = new ChatModifierRp();
	public static ChatModifierRp get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getModified(String subject, CommandSender sender, CommandSender recipient)
	{
		if (subject.equals("")) return subject;
		return subject+" ";
	}

}
