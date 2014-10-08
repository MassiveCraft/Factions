package com.massivecraft.factions.integration.lwc;

import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import com.griefcraft.lwc.LWC;
import com.griefcraft.model.Protection;
import com.griefcraft.scripting.JavaModule;
import com.griefcraft.scripting.event.LWCProtectionInteractEvent;
import com.griefcraft.scripting.event.LWCProtectionRegisterEvent;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.engine.EngineMain;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.IdUtil;
import com.massivecraft.massivecore.util.SmokeUtil;
import com.massivecraft.massivecore.util.Txt;

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
	// 
	
	@Override
	public void onRegisterProtection(LWCProtectionRegisterEvent event)
	{
		// If this feature is enabled ...
		if ( ! MConf.get().lwcMustHaveBuildRightsToCreate) return;
		
		// ... and the player don't have build rights here ...
		// NOTE: We verbosely check the build rights so that a proper info message is sent 
		if (EngineMain.canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), true)) return;
		
		// ... then cancel the event.
		event.setCancelled(true);
	}
	
	@Override
	public void onProtectionInteract(LWCProtectionInteractEvent event)
	{
		// If this feature is enabled ...
		if ( ! MConf.get().lwcRemoveIfNoBuildRights) return;
		
		// ... gather data ...
		final Protection protection = event.getProtection();
		final Block block = protection.getBlock();
		final PS ps = PS.valueOf(block);
		// NOTE: The LWC protection owner is still the name and not the UUID. For that reason we must convert it. 
		final String ownerName = protection.getOwner();
		final String ownerId = IdUtil.getId(ownerName);
		final MPlayer mowner = MPlayer.get(ownerId);
		if (mowner == null) return;
		
		// ... and if the protection owner no longer has build rights for the area ...
		// NOTE: We silently check the build rights for the protection owner.
		// NOTE: The protection owner may even be offline at the moment.
		if (EngineMain.canPlayerBuildAt(mowner, ps, false)) return;
		
		// ... remove the protection ...
		protection.remove();
		
		// ... cancel the event ...
		// NOTE: The first time you click nothing but the unlock should happen.
		// NOTE: This way it's more obvious the auto unlock system kicked in.
		// NOTE: No inventory will get opened.
		event.setResult(Result.CANCEL);
		
		// ... play FX ...
		Location location = block.getLocation();
		SmokeUtil.spawnCloudSimple(location);
		location.getWorld().playSound(location, Sound.DOOR_OPEN, 1, 1);
		
		// ... and inform.
		Player player = event.getPlayer();
		String message = Txt.parse("<i>Factions removed <h>%s's <i>LWC. They lacked build rights.", mowner.getDisplayName(player));
		player.sendMessage(message);
	}
	
}
