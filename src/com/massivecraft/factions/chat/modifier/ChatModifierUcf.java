package com.massivecraft.factions.chat.modifier;

import com.massivecraft.factions.chat.ChatModifierAbstract;
import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.mcore.util.Txt;

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
	public String getModified(String subject, FPlayer fsender, FPlayer frecipient)
	{
		return Txt.upperCaseFirst(subject);
	}

}
