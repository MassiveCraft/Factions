package com.massivecraft.factions.chat.modifier;

import com.massivecraft.factions.chat.ChatModifierAbstract;
import com.massivecraft.factions.entity.FPlayer;

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
	public String getModified(String subject, FPlayer fsender, FPlayer frecipient)
	{
		return subject.toLowerCase();
	}

}
