package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.FPlayer;

public class ChatTagTitle extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagTitle() { super("factions_title"); }
	private static ChatTagTitle i = new ChatTagTitle();
	public static ChatTagTitle get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(FPlayer fsender, FPlayer frecipient)
	{
		return fsender.getTitle();
	}

}
