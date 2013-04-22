package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.FPlayerColl;

public class ChatTagRelcolor extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagRelcolor() { super("factions_relcolor"); }
	private static ChatTagRelcolor i = new ChatTagRelcolor();
	public static ChatTagRelcolor get() { return i; }
	
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
