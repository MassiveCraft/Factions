package com.massivecraft.factions.chat;

import org.bukkit.command.CommandSender;

public interface ChatTag
{
	public String getId();
	public String getReplacement(CommandSender sender, CommandSender recipient);
	public boolean register();
	public boolean unregister();
}
