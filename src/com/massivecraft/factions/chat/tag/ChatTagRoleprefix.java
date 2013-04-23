package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.UPlayer;

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
	public String getReplacement(UPlayer fsender, UPlayer frecipient)
	{
		return fsender.getRole().getPrefix();
	}

}
