package com.massivecraft.factions.chat.modifier;

import com.massivecraft.factions.chat.ChatModifierAbstract;
import com.massivecraft.factions.entity.UPlayer;

public class ChatModifierRp extends ChatModifierAbstract
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
	public String getModified(String subject, UPlayer fsender, UPlayer frecipient)
	{
		if (subject.equals("")) return subject;
		return subject+" ";
	}

}
