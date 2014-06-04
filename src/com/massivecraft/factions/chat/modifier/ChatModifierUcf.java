package com.massivecraft.factions.chat.modifier;

import org.bukkit.command.CommandSender;

import com.massivecraft.factions.chat.ChatModifierAbstract;
import com.massivecraft.massivecore.util.Txt;

public class ChatModifierUcf extends ChatModifierAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatModifierUcf() { super("ucf"); }
	private static ChatModifierUcf i = new ChatModifierUcf();
	public static ChatModifierUcf get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getModified(String subject, CommandSender sender, CommandSender recipient)
	{
		return Txt.upperCaseFirst(subject);
	}

}
