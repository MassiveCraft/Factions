package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.mcore.mixin.Mixin;

public class ChatTagSendeeId extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagSendeeId() { super("sendeeid"); }
	private static ChatTagSendeeId i = new ChatTagSendeeId();
	public static ChatTagSendeeId get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{
		return Mixin.tryFix(sendeeId);
	}

}
