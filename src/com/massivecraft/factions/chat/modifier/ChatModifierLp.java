package com.massivecraft.factions.chat.modifier;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatModifier;


public class ChatModifierLp extends ChatModifier
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatModifierLp() { super("lp"); }
	private static ChatModifierLp i = new ChatModifierLp();
	public static ChatModifierLp get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getModified(String subject, CommandSender sender, CommandSender recipient)
	{
		if (subject.equals("")) return subject;
		return " "+subject;
	}

}
