package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.UPlayer;

public class ChatTagRelcolor extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private ChatTagRelcolor() { super("factions_relcolor"); }
	private static ChatTagRelcolor i = new ChatTagRelcolor();
	public static ChatTagRelcolor get() { return i; }

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(UPlayer fsender, UPlayer frecipient)
	{
		if (fsender == null) return "";
		if (frecipient == null) return "";

		return frecipient.getRelationTo(fsender).getColor().toString();
	}

}
