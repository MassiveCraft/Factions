package com.massivecraft.factions.chat.modifier;

import com.massivecraft.factions.chat.ChatModifierAbstract;
import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.mcore.util.Txt;

public class ChatModifierParse extends ChatModifierAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatModifierParse() { super("parse"); }
	private static ChatModifierParse i = new ChatModifierParse();
	public static ChatModifierParse get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getModified(String subject, FPlayer fsender, FPlayer frecipient)
	{
		return Txt.parse(subject);
	}

}
