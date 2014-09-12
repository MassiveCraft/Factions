package com.massivecraft.factions.integration.dynmap;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import com.massivecraft.factions.Factions;
import com.massivecraft.massivecore.integration.IntegrationAbstract;
import com.massivecraft.massivecore.util.Txt;

public class IntegrationDynmapFactions extends IntegrationAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static IntegrationDynmapFactions i = new IntegrationDynmapFactions();
	public static IntegrationDynmapFactions get() { return i; }
	private IntegrationDynmapFactions() { super("Dynmap-Factions"); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void activate()
	{
		// Time for an error message!
		Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				Factions.get().log(Txt.parse("<b>I see you have the plugin Dynmap-Factions installed!"));
				Factions.get().log(Txt.parse("<b>That plugin is no longer required for Dynmap features."));
				Factions.get().log(Txt.parse("<b>Factions now ship with it's own Dynmap integration."));
				Factions.get().log(Txt.parse("<b>Now disabling Dynmap-Factions for you:"));
				
				Plugin plugin = Bukkit.getPluginManager().getPlugin("Dynmap-Factions");
				Bukkit.getPluginManager().disablePlugin(plugin);
			}
		});
	}
	
}
