package com.massivecraft.factions.chat.modifier;

import com.massivecraft.factions.chat.ChatModifier;
import org.bukkit.command.CommandSender;

public class ChatModifierUc extends ChatModifier
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatModifierUc() { super("uc"); }
	private static ChatModifierUc i = new ChatModifierUc();
	public static ChatModifierUc get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getModified(String subject, CommandSender sender, CommandSender recipient)
	{
		return subject.toUpperCase();
	}

}
