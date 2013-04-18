package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.chat.ChatTagAbstract;

public class ChatTagFactionRelcolor extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagFactionRelcolor() { super("factions_relcolor"); }
	private static ChatTagFactionRelcolor i = new ChatTagFactionRelcolor();
	public static ChatTagFactionRelcolor get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{
		if (senderId == null) return "";
		if (recipientId == null) return "";
		
		FPlayer fsender = FPlayerColl.get().get(senderId);
		FPlayer frecipient = FPlayerColl.get().get(recipientId);
		
		if (fsender == null) return "";
		if (frecipient == null) return "";
		
		return frecipient.getRelationTo(fsender).getColor().toString();
	}

}
