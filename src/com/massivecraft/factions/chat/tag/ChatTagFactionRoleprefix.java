package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.chat.ChatTagAbstract;

public class ChatTagFactionRoleprefix extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagFactionRoleprefix() { super("factions_roleprefix"); }
	private static ChatTagFactionRoleprefix i = new ChatTagFactionRoleprefix();
	public static ChatTagFactionRoleprefix get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{		
		FPlayer fsender = FPlayerColl.get().get(senderId);
		return fsender.getRole().getPrefix();
	}

}
