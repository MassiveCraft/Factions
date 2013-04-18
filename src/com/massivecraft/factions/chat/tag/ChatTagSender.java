package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.mcore.mixin.Mixin;

public class ChatTagSender extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagSender() { super("sender"); }
	private static ChatTagSender i = new ChatTagSender();
	public static ChatTagSender get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(String senderId, String sendeeId, String recipientId)
	{
		return Mixin.getDisplayName(senderId);
	}

}
