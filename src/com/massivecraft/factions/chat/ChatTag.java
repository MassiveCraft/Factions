package com.massivecraft.factions.chat;

public interface ChatTag
{
	public String getId();
	public String getReplacement(String senderId, String sendeeId, String recipientId);
	public boolean register();
	public boolean unregister();
}
