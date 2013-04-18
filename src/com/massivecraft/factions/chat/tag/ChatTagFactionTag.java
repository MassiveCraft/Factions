package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.chat.ChatTagAbstract;

public class ChatTagFactionTag extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagFactionTag() { super("factions_tag"); }
	private static ChatTagFactionTag i = new ChatTagFactionTag();
	public static ChatTagFactionTag get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{		
		FPlayer fsender = FPlayerColl.get().get(senderId);
		if (!fsender.hasFaction()) return "";
		return fsender.getFaction().getTag();
	}

}
