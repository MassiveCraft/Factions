package com.massivecraft.factions.chat;

import com.massivecraft.factions.entity.UPlayer;

public interface ChatTag
{
	public String getId();
	public String getReplacement(UPlayer fsender, UPlayer frecipient);
	public boolean register();
	public boolean unregister();
}
