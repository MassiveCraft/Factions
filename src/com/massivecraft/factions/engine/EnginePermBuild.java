package com.massivecraft.factions.engine;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.integration.spigot.IntegrationSpigot;
import com.massivecraft.factions.util.EnumerationUtil;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.Map;

public class EnginePermBuild extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EnginePermBuild i = new EnginePermBuild();
	public static EnginePermBuild get() { return i; }

	// -------------------------------------------- //
	// PERM: BUILD
	// -------------------------------------------- //

	public static boolean canPlayerBuildAt(Object senderObject, PS ps, boolean verboose)
	{
		MPlayer mplayer = MPlayer.get(senderObject);
		if (mplayer == null) return false;

		String name = mplayer.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		if (mplayer.isOverriding()) return true;

		if (!MPerm.getPermBuild().has(mplayer, ps, false) && MPerm.getPermPainbuild().has(mplayer, ps, false))
		{
			if (verboose)
			{
				Faction hostFaction = BoardColl.get().getFactionAt(ps);
				mplayer.msg("<b>It is painful to build in the territory of %s<b>.", hostFaction.describeTo(mplayer));
				Player player = mplayer.getPlayer();
				if (player != null)
				{
					player.damage(MConf.get().actionDeniedPainAmount);
				}
			}
			return true;
		}

		return MPerm.getPermBuild().has(mplayer, ps, verboose);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void blockBuild(BlockPlaceEvent event)
	{
		if (!event.canBuild()) return;

		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), verboose)) return;

		event.setBuild(false);
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockBreakEvent event)
	{
		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), verboose)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockDamageEvent event)
	{
		if ( ! event.getInstaBreak()) return;

		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), verboose)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(SignChangeEvent event)
	{
		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), verboose)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonExtendEvent event)
	{
		// Is using Spigot or is checking deactivated by MConf?
		if (IntegrationSpigot.get().isIntegrationActive() || ! MConf.get().handlePistonProtectionThroughDenyBuild) return;

		Block block = event.getBlock();

		// Targets end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);

		// Factions involved
		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(block));
		Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetBlock));

		// Members of a faction might not have build rights in their own territory, but pistons should still work regardless
		if (targetFaction == pistonFaction) return;

		// if potentially pushing into air/water/lava in another territory, we need to check it out
		if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && ! MPerm.getPermBuild().has(pistonFaction, targetFaction))
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
		// Is using Spigot or is checking deactivated by MConf?
		if (IntegrationSpigot.get().isIntegrationActive() || ! MConf.get().handlePistonProtectionThroughDenyBuild) return;

		// If not a sticky piston, retraction should be fine
		if ( ! event.isSticky()) return;

		Block retractBlock = event.getRetractLocation().getBlock();
		PS retractPs = PS.valueOf(retractBlock);

		// if potentially retracted block is just air/water/lava, no worries
		if (retractBlock.isEmpty() || retractBlock.isLiquid()) return;

		// Factions involved
		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(event.getBlock()));
		Faction targetFaction = BoardColl.get().getFactionAt(retractPs);

		// Members of a faction might not have build rights in their own territory, but pistons should still work regardless
		if (targetFaction == pistonFaction) return;

		if (MPerm.getPermBuild().has(pistonFaction, targetFaction)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(HangingPlaceEvent event)
	{
		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getEntity().getLocation()), verboose)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(HangingBreakEvent event)
	{
		if (! (event instanceof HangingBreakByEntityEvent)) return;
		HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent)event;

		Entity breaker = entityEvent.getRemover();
		if (MUtil.isntPlayer(breaker)) return;

		boolean verboose = ! isFake(event);

		if ( ! canPlayerBuildAt(breaker, PS.valueOf(event.getEntity().getLocation()), verboose))
		{
			event.setCancelled(true);
		}
	}

	// Check for punching out fires where players should not be able to
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void blockBuild(PlayerInteractEvent event)
	{
		// ... if it is a left click on block ...
		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;

		// .. and the clicked block is not null ...
		if (event.getClickedBlock() == null) return;

		Block potentialBlock = event.getClickedBlock().getRelative(BlockFace.UP, 1);

		// .. and the potential block is not null ...
		if (potentialBlock == null) return;

		// ... and we're only going to check for fire ... (checking everything else would be bad performance wise)
		if (potentialBlock.getType() != Material.FIRE) return;

		// ... check if they can build ...
		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(potentialBlock), true)) return;

		// ... nope, cancel it
		event.setCancelled(true);

		// .. and compensate for client side prediction
		event.getPlayer().sendBlockChange(potentialBlock.getLocation(), potentialBlock.getType(), potentialBlock.getState().getRawData());
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockLiquidFlow(BlockFromToEvent event)
	{
		if ( ! MConf.get().protectionLiquidFlowEnabled) return;

		// Prepare fields
		Block fromBlock = event.getBlock();
		int fromChunkX = fromBlock.getX() >> 4;
		int fromChunkZ = fromBlock.getZ() >> 4;
		BlockFace blockFace = event.getFace();
		int toChunkX = (fromBlock.getX() + blockFace.getModX()) >> 4;
		int toChunkZ = (fromBlock.getZ() + blockFace.getModZ()) >> 4;

		// If a liquid (or dragon egg) moves from one chunk to another ...
		if (toChunkX == fromChunkX && toChunkZ == fromChunkZ) return;

		Board board = BoardColl.get().getFixed(fromBlock.getWorld().getName().toLowerCase(), false);
		if (board == null) return;
		Map<PS, TerritoryAccess> map = board.getMapRaw();
		if (map.isEmpty()) return;

		PS fromPs = PS.valueOf(fromChunkX, fromChunkZ);
		PS toPs = PS.valueOf(toChunkX, toChunkZ);
		TerritoryAccess fromTerritoryAccess = map.get(fromPs);
		TerritoryAccess toTerritoryAccess = map.get(toPs);
		String fromFactionId = fromTerritoryAccess != null ? fromTerritoryAccess.getHostFactionId() : Factions.ID_NONE;
		String toFactionId = toTerritoryAccess != null ? toTerritoryAccess.getHostFactionId() : Factions.ID_NONE;

		// ... and the chunks belong to different factions ...
		if (toFactionId.equals(fromFactionId)) return;
		
		Faction fromFaction = FactionColl.get().getFixed(fromFactionId);
		if (fromFaction == null) fromFaction = FactionColl.get().getNone();
		
		Faction toFaction = FactionColl.get().getFixed(toFactionId);
		if (toFaction == null) toFaction = FactionColl.get().getNone();
		
		if (toFaction == fromFaction) return;
		
		// ... and the faction "from" can not build at "to" ...
		if (MPerm.getPermBuild().has(fromFaction, toFaction)) return;

		// ... cancel!
		event.setCancelled(true);
	}

	// -------------------------------------------- //
	// ASSORTED BUILD AND INTERACT
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDamageEntity(EntityDamageByEntityEvent event)
	{
		// If a player ...
		Entity edamager = MUtil.getLiableDamager(event);
		if (MUtil.isntPlayer(edamager)) return;
		Player player = (Player)edamager;

		// ... damages an entity which is edited on damage ...
		Entity edamagee = event.getEntity();
		if (edamagee == null) return;
		if ( ! EnumerationUtil.isEntityTypeEditOnDamage(edamagee.getType())) return;

		// ... and the player can't build there ...
		if (canPlayerBuildAt(player, PS.valueOf(edamagee.getLocation()), true)) return;

		// ... then cancel the event.
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		// only need to check right-clicks and physical as of MC 1.4+; good performance boost
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) return;

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null) return;  // clicked in air, apparently

		if ( ! canPlayerUseBlock(player, block, true))
		{
			event.setCancelled(true);
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;  // only interested on right-clicks for below

		if ( ! playerCanUseItemHere(player, PS.valueOf(block), event.getMaterial(), true))
		{
			event.setCancelled(true);
			return;
		}
	}

	public static boolean playerCanUseItemHere(Player player, PS ps, Material material, boolean verboose)
	{
		if (MUtil.isntPlayer(player)) return true;
		
		if ( ! EnumerationUtil.isMaterialEditTool(material)) return true;

		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		MPlayer mplayer = MPlayer.get(player);
		if (mplayer.isOverriding()) return true;

		return MPerm.getPermBuild().has(mplayer, ps, verboose);
	}

	public static boolean canPlayerUseBlock(Player player, Block block, boolean verboose)
	{
		if (MUtil.isntPlayer(player)) return true;

		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		MPlayer me = MPlayer.get(player);
		if (me.isOverriding()) return true;

		PS ps = PS.valueOf(block);
		Material material = block.getType();

		if (EnumerationUtil.isMaterialEditOnInteract(material) && ! MPerm.getPermBuild().has(me, ps, verboose)) return false;
		if (EnumerationUtil.isMaterialContainer(material) && ! MPerm.getPermContainer().has(me, ps, verboose)) return false;
		if (EnumerationUtil.isMaterialDoor(material) && ! MPerm.getPermDoor().has(me, ps, verboose)) return false;
		if (material == Material.STONE_BUTTON && ! MPerm.getPermButton().has(me, ps, verboose)) return false;
		if (material == Material.LEVER && ! MPerm.getPermLever().has(me, ps, verboose)) return false;
		return true;
	}

	// This event will not fire for Minecraft 1.8 armor stands.
	// Armor stands are handled in EngineSpigot instead.
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		// Ignore Off Hand
		if (isOffHand(event)) return;

		// Gather Info
		final Player player = event.getPlayer();
		final Entity entity = event.getRightClicked();
		final boolean verboose = true;

		// If we can't use ...
		if (canPlayerUseEntity(player, entity, verboose)) return;

		// ... block use.
		event.setCancelled(true);
	}

	public static boolean canPlayerUseEntity(Player player, Entity entity, boolean verboose)
	{
		// If a player ...
		if (MUtil.isntPlayer(player)) return true;

		// ... interacts with an entity ...
		if (entity == null) return true;
		EntityType type = entity.getType();
		PS ps = PS.valueOf(entity.getLocation());

		// ... and the player does not bypass all protections ...
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		// ... and the player is not using admin mode ...
		MPlayer me = MPlayer.get(player);
		if (me.isOverriding()) return true;

		// ... check container entity rights ...
		if (EnumerationUtil.isEntityTypeContainer(type) && ! MPerm.getPermContainer().has(me, ps, verboose)) return false;

		// ... check build entity rights ...
		if (EnumerationUtil.isEntityTypeEditOnInteract(type) && ! MPerm.getPermBuild().has(me, ps, verboose)) return false;

		// ... otherwise we may use the entity.
		return true;
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (playerCanUseItemHere(player, PS.valueOf(block), event.getBucket(), true)) return;

		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (playerCanUseItemHere(player, PS.valueOf(block), event.getBucket(), true)) return;

		event.setCancelled(true);
	}

}
