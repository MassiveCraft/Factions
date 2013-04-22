package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.FPlayer;

public class ChatTagRoleprefix extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagRoleprefix() { super("factions_roleprefix"); }
	private static ChatTagRoleprefix i = new ChatTagRoleprefix();
	public static ChatTagRoleprefix get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(FPlayer fsender, FPlayer frecipient)
	{		
		return fsender.getRole().getPrefix();
	}

}
