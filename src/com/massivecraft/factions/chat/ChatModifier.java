package com.massivecraft.factions.chat;

import com.massivecraft.factions.entity.FPlayer;


public interface ChatModifier
{
	public String getId();
	public String getModified(String subject, FPlayer fsender, FPlayer frecipient);
	public boolean register();
	public boolean unregister();
}
