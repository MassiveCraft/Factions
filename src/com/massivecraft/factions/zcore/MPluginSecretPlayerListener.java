package com.massivecraft.factions.zcore;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import com.massivecraft.factions.zcore.persist.EM;
import com.massivecraft.factions.zcore.persist.Entity;
import com.massivecraft.factions.zcore.persist.EntityCollection;
import com.massivecraft.factions.zcore.persist.PlayerEntityCollection;

public class MPluginSecretPlayerListener implements Listener
{
	public MPlugin p;
	public MPluginSecretPlayerListener(MPlugin p)
	{
		this.p = p;
		Bukkit.getPluginManager().registerEvents(this, this.p);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerPreLogin(PlayerLoginEvent event)
	{
		for (EntityCollection<? extends Entity> ecoll : EM.class2Entities.values())
		{
			if (ecoll instanceof PlayerEntityCollection)
			{
				ecoll.get(event.getPlayer().getName());
			}
		}
	}
}
