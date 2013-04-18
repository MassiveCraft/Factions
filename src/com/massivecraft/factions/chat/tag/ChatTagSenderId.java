package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.mcore.mixin.Mixin;

public class ChatTagSenderId extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagSenderId() { super("senderid"); }
	private static ChatTagSenderId i = new ChatTagSenderId();
	public static ChatTagSenderId get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{
		return Mixin.tryFix(senderId);
	}

}
