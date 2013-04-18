package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.chat.ChatTagAbstract;

public class ChatTagFactionTagforce extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagFactionTagforce() { super("factions_tagforce"); }
	private static ChatTagFactionTagforce i = new ChatTagFactionTagforce();
	public static ChatTagFactionTagforce get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{		
		FPlayer fsender = FPlayerColl.get().get(senderId);
		return fsender.getFaction().getTag();
	}

}
