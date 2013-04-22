package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.mcore.util.Txt;

public class ChatTagRole extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagRole() { super("factions_role"); }
	private static ChatTagRole i = new ChatTagRole();
	public static ChatTagRole get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(UPlayer fsender, UPlayer frecipient)
	{		
		return Txt.upperCaseFirst(fsender.getRole().toString().toLowerCase());
	}

}
