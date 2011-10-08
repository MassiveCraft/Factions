package com.massivecraft.factions.zcore;

import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerPreLoginEvent;

import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;

public class MPluginSecretPlayerListener extends PlayerListener
{
	private MPlugin p;
	public MPluginSecretPlayerListener(MPlugin p)
	{
		this.p = p;
	}
	
	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (event.isCancelled()) return;

		if (p.handleCommand(event.getPlayer(), event.getMessage()))
		{
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		
		if (p.handleCommand(event.getPlayer(), event.getMessage()))
		{
			event.setCancelled(true);
		}
	}
	
	@Override
	public void onPlayerPreLogin(PlayerPreLoginEvent event)
	{
		for (EntityCollection<? extends Entity> ecoll : EM.class2Entities.values())
		{
			if (ecoll instanceof PlayerEntityCollection)
			{
				ecoll.get(event.getName());
			}
		}
	}
	
}
