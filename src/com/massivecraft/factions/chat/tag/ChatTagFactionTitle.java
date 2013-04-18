package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.chat.ChatTagAbstract;

public class ChatTagFactionTitle extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagFactionTitle() { super("factions_title"); }
	private static ChatTagFactionTitle i = new ChatTagFactionTitle();
	public static ChatTagFactionTitle get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{		
		FPlayer fsender = FPlayerColl.get().get(sendeeId);
		return fsender.getTitle();
	}

}
