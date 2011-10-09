package com.massivecraft.factions.zcore.persist;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerEntity extends Entity
{
	public Player getPlayer()
	{
		return Bukkit.getPlayer(this.getId());
	}
	
	public boolean isOnline()
	{
		return this.getPlayer() != null;
	}
	
	public boolean isOffline()
	{
		return ! isOnline();
	}
	
	// -------------------------------------------- //
	// Message Sending Helpers
	// -------------------------------------------- //
	
	public void sendMessage(String msg)
	{
		Player player = this.getPlayer();
		if (player == null) return;
		player.sendMessage(msg);
	}
	
	public void sendMessage(List<String> msgs)
	{
		for(String msg : msgs)
		{
			this.sendMessage(msg);
		}
	}
	
}
