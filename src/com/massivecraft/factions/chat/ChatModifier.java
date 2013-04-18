package com.massivecraft.factions.chat;


public interface ChatModifier
{
	public String getId();
	public String getModified(String subject, String senderId, String sendeeId, String recipientId);
	public boolean register();
	public boolean unregister();
}
