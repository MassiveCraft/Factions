package com.massivecraft.factions.chat;

import com.massivecraft.factions.entity.FPlayer;

public interface ChatTag
{
	public String getId();
	public String getReplacement(FPlayer fsender, FPlayer frecipient);
	public boolean register();
	public boolean unregister();
}
