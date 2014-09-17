package com.massivecraft.factions.chat;

import org.bukkit.command.CommandSender;

public interface ChatModifier
{
	public String getId();
	public String getModified(String subject, CommandSender sender, CommandSender recipient);
	public boolean register();
	public boolean unregister();
}
