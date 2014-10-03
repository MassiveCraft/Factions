package com.massivecraft.factions.integration.lwc;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCBlockInteractEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.listeners.FactionsListenerMain;
import com.massivecraft.massivecore.ps.PS;

@SuppressWarnings("unused")
public class FactionsLwcModule extends JavaModule
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	// These plugin variables must be present.
	// They are set by LWC using reflection somehow.
	private Factions plugin;
	private LWC lwc;

	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public FactionsLwcModule(Factions plugin)
	{
		this.plugin = plugin;
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void onRegisterProtection(LWCProtectionRegisterEvent event)
	{
		if (FactionsListenerMain.canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), false)) return;
		event.setCancelled(true);
	}
	
	@Override
	public void onBlockInteract(LWCBlockInteractEvent event) {
		Protection protection = event.getLWC().findProtection(event.getBlock());
		if (protection == null) return;
		
		// Check the existing owner of this protection 
		if (FactionsListenerMain.canPlayerBuildAt(protection.getBukkitOwner(), PS.valueOf(event.getBlock()), false)) return;
		
		protection.remove();
	}
}
