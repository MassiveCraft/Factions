package com.massivecraft.factions.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;

import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.mcore.ps.PS;

public class FactionsListenerMain implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionsListenerMain i = new FactionsListenerMain();
	public static FactionsListenerMain get() { return i; }
	public FactionsListenerMain() {}
	
	// -------------------------------------------- //
	// SETUP
	// -------------------------------------------- //
	
	public void setup()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
	}
	
	// -------------------------------------------- //
	// FLAG: EXPLOSIONS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(HangingBreakEvent event)
	{
		// If a hanging entity was broken by an explosion ...
		if (event.getCause() != RemoveCause.EXPLOSION) return;
	
		// ... and the faction there has explosions disabled ...
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(event.getEntity()));
		if (faction.getFlag(FFlag.EXPLOSIONS)) return;
		
		// ... then cancel.
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// FLAG: BUILD
	// -------------------------------------------- //

	public static boolean canPlayerBuildAt(Player player, PS ps, boolean justCheck)
	{
		String name = player.getName();
		if (ConfServer.playersWhoBypassAllProtection.contains(name)) return true;

		FPlayer me = FPlayer.get(name);
		if (me.isUsingAdminMode()) return true;

		Faction factionHere = BoardColl.get().getFactionAt(ps);

		if ( ! FPerm.BUILD.has(me, ps) && FPerm.PAINBUILD.has(me, ps))
		{
			if (!justCheck)
			{
				me.msg("<b>It is painful to build in the territory of %s<b>.", factionHere.describeTo(me));
				player.damage(ConfServer.actionDeniedPainAmount);
			}
			return true;
		}
		
		return FPerm.BUILD.has(me, ps, true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(HangingPlaceEvent event)
	{
		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getEntity()), false)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(HangingBreakEvent event)
	{
		if (! (event instanceof HangingBreakByEntityEvent)) return;
		HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent)event;
		
		Entity breaker = entityEvent.getRemover();
		if (! (breaker instanceof Player)) return;

		if ( ! canPlayerBuildAt((Player)breaker, PS.valueOf(event.getEntity()), false))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void blockBuild(BlockPlaceEvent event)
	{
		if (!event.canBuild()) return;

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), false)) return;
		
		event.setBuild(false);
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockBreakEvent event)
	{
		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), false)) return;
		
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockDamageEvent event)
	{
		if (!event.getInstaBreak()) return;

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), false)) return;
		
		event.setCancelled(true);
	}
	

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonExtendEvent event)
	{
		if ( ! ConfServer.pistonProtectionThroughDenyBuild) return;

		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(event.getBlock()));

		// target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
		Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

		// members of faction might not have build rights in their own territory, but pistons should still work regardless; so, address that corner case
		Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetBlock));
		if (targetFaction == pistonFaction) return;

		// if potentially pushing into air/water/lava in another territory, we need to check it out
		if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && ! FPerm.BUILD.has(pistonFaction, targetBlock.getLocation()))
		{
			event.setCancelled(true);
		}

		/*
		 * note that I originally was testing the territory of each affected block, but since I found that pistons can only push
		 * up to 12 blocks and the width of any territory is 16 blocks, it should be safe (and much more lightweight) to test
		 * only the final target block as done above
		 */
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonRetractEvent event)
	{
		if (!ConfServer.pistonProtectionThroughDenyBuild) return;
		
		// if not a sticky piston, retraction should be fine
		if (!event.isSticky()) return;

		Location targetLoc = event.getRetractLocation();

		// if potentially retracted block is just air/water/lava, no worries
		if (targetLoc.getBlock().isEmpty() || targetLoc.getBlock().isLiquid()) return;

		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(event.getBlock()));

		// members of faction might not have build rights in their own territory, but pistons should still work regardless; so, address that corner case
		Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetLoc));
		if (targetFaction == pistonFaction) return;

		if ( ! FPerm.BUILD.has(pistonFaction, targetLoc))
		{
			event.setCancelled(true);
		}
	}
	
	// -------------------------------------------- //
	// FLAG: FIRE SPREAD
	// -------------------------------------------- //
	
	public void blockFireSpread(Block block, Cancellable cancellable)
	{
		// If the faction at the block has firespread disabled ...
		PS ps = PS.valueOf(block);
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction.getFlag(FFlag.FIRESPREAD)) return;
		
		// then cancel the event.
		cancellable.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockIgniteEvent event)
	{
		// If fire is spreading ...
		if (event.getCause() != IgniteCause.SPREAD && event.getCause() != IgniteCause.LAVA) return;
		
		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}
	
	// TODO: Is use of this event deprecated?
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockSpreadEvent event)
	{
		// If fire is spreading ...
		if (event.getNewState().getTypeId() != 51) return;
		
		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockBurnEvent event)
	{
		// If a block is burning ...
		
		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}
	
	// -------------------------------------------- //
	// SPOUT
	// -------------------------------------------- //
	// TODO: These spout related methods should not be in here.
	// The spout integration needs to be moved elsewhere.
	// NOTE: Also the spout integration should not have method calls from within FactionsCore code,
	// we should instead listen to FactionsCore events. And send client updates upon non-cancelled monitor.
	
	// Setup
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void spoutSetup(PluginDisableEvent event)
	{
		SpoutFeatures.setup();
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void spoutSetup(PluginEnableEvent event)
	{
		SpoutFeatures.setup();
	}
	
	// Standard
	
	public static void spoutStandard(Player player)
	{
		SpoutFeatures.updateTitleShortly(player, null);
		SpoutFeatures.updateTitleShortly(null, player);
		SpoutFeatures.updateCapeShortly(player, null);
		SpoutFeatures.updateCapeShortly(null, player);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void spoutStandard(PlayerJoinEvent event)
	{		
		spoutStandard(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spoutStandard(PlayerTeleportEvent event)
	{
		if (event.getFrom().getWorld().equals(event.getTo().getWorld())) return;
		spoutStandard(event.getPlayer());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void spoutStandard(PlayerRespawnEvent event)
	{
		spoutStandard(event.getPlayer());
	}
	
	// Health Bar
	
	public static void spoutHealthBar(Entity entity)
	{
		if ( ! ConfServer.spoutHealthBarUnderNames) return;
		if ( ! (entity instanceof Player)) return;
		Player player = (Player)entity;
		SpoutFeatures.updateTitle(player, null);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spoutHealthBar(EntityDamageEvent event)
	{
		spoutHealthBar(event.getEntity());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spoutHealthBar(EntityRegainHealthEvent event)
	{
		spoutHealthBar(event.getEntity());
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void spoutHealthBar(PlayerRespawnEvent event)
	{
		spoutHealthBar(event.getPlayer());
	}
	
}
