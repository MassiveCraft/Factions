package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.mcore.util.Txt;

public class ChatTagFactionRole extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagFactionRole() { super("factions_role"); }
	private static ChatTagFactionRole i = new ChatTagFactionRole();
	public static ChatTagFactionRole get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{		
		FPlayer fsender = FPlayerColl.get().get(senderId);
		return Txt.upperCaseFirst(fsender.getRole().toString().toLowerCase());
	}

}
