package com.massivecraft.factions.chat.modifier;

import com.massivecraft.factions.chat.ChatModifierAbstract;


public class ChatModifierLp extends ChatModifierAbstract
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
	public String getModified(String subject, String senderId, String sendeeId, String recipientId)
	{
		if (subject.equals("")) return subject;
		return " "+subject;
	}

}
