package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.mcore.mixin.Mixin;

public class ChatTagSendee extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagSendee() { super("sendee"); }
	private static ChatTagSendee i = new ChatTagSendee();
	public static ChatTagSendee get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{
		return Mixin.getDisplayName(sendeeId);
	}

}
