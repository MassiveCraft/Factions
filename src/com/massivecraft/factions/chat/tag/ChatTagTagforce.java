package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.FPlayer;

public class ChatTagTagforce extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagTagforce() { super("factions_tagforce"); }
	private static ChatTagTagforce i = new ChatTagTagforce();
	public static ChatTagTagforce get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(FPlayer fsender, FPlayer frecipient)
	{
		return fsender.getFaction().getTag();
	}

}
