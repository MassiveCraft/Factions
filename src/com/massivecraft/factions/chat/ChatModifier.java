package com.massivecraft.factions.chat;

import com.massivecraft.factions.entity.UPlayer;


public interface ChatModifier
{
	public String getId();
	public String getModified(String subject, UPlayer fsender, UPlayer frecipient);
	public boolean register();
	public boolean unregister();
}
