package com.massivecraft.factions.chat.modifier;

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
	public String getModified(String subject, String senderId, String sendeeId, String recipientId)
	{
		return subject.toLowerCase();
	}

}
