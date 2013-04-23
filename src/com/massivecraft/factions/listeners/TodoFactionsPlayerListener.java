package com.massivecraft.factions.listeners;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.util.Txt;


public class TodoFactionsPlayerListener implements Listener
{
	// -------------------------------------------- //
	// TERRITORY INFO MESSAGES
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		// If the player is moving from one chunk to another ...
		if (MUtil.isSameChunk(event)) return;
		
		// ... gather info on the player and the move ...
		Player player = event.getPlayer();
		UPlayer uplayerTo = UPlayerColls.get().get(event.getTo()).get(player);
		
		PS chunkFrom = PS.valueOf(event.getFrom()).getChunk(true);
		PS chunkTo = PS.valueOf(event.getTo()).getChunk(true);
		
		Faction factionFrom = BoardColls.get().getFactionAt(chunkFrom);
		Faction factionTo = BoardColls.get().getFactionAt(chunkTo);
		
		// ... send host faction info updates ...
		if (uplayerTo.isMapAutoUpdating())
		{
			uplayerTo.sendMessage(BoardColls.get().getMap(uplayerTo.getFaction(), chunkTo, player.getLocation().getYaw()));
		}
		else if (factionFrom != factionTo)
		{
			String msg = Txt.parse("<i>") + " ~ " + factionTo.getTag(uplayerTo);
			if (factionTo.hasDescription())
			{
				msg += " - " + factionTo.getDescription();
			}
			player.sendMessage(msg);
		}

		// show access info message if needed
		TerritoryAccess accessTo = BoardColls.get().getTerritoryAccessAt(chunkTo);
		if (!accessTo.isDefault())
		{
			if (accessTo.subjectHasAccess(uplayerTo))
			{
				uplayerTo.msg("<g>You have access to this area.");
			}
			else if (accessTo.subjectAccessIsRestricted(uplayerTo))
			{
				uplayerTo.msg("<b>This area has restricted access.");
			}
		}

		if (uplayerTo.getAutoClaimFor() != null)
		{
			uplayerTo.attemptClaim(uplayerTo.getAutoClaimFor(), PS.valueOf(event.getTo()), true);
		}
	}

	// -------------------------------------------- //
	// ASSORTED BUILD AND INTERACT
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		// only need to check right-clicks and physical as of MC 1.4+; good performance boost
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) return;

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null) return;  // clicked in air, apparently

		if ( ! canPlayerUseBlock(player, block, false))
		{
			event.setCancelled(true);
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;  // only interested on right-clicks for below

		if ( ! playerCanUseItemHere(player, PS.valueOf(block), event.getMaterial(), false))
		{
			event.setCancelled(true);
			return;
		}
	}

	// TODO: Refactor ! justCheck    -> to informIfNot
	// TODO: Possibly incorporate pain build... 
	public static boolean playerCanUseItemHere(Player player, PS ps, Material material, boolean justCheck)
	{
		if (!Const.MATERIALS_EDIT_TOOLS.contains(material)) return true;
		
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		UPlayer uplayer = UPlayer.get(player);
		if (uplayer.isUsingAdminMode()) return true;
		
		return FPerm.BUILD.has(uplayer, ps, !justCheck);
	}
	
	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck)
	{
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		UPlayer me = UPlayer.get(player);
		if (me.isUsingAdminMode()) return true;
		
		PS ps = PS.valueOf(block);
		Material material = block.getType();
		
		if (Const.MATERIALS_EDIT_ON_INTERACT.contains(material) && ! FPerm.BUILD.has(me, ps, ! justCheck)) return false;
		if (Const.MATERIALS_CONTAINER.contains(material) && ! FPerm.CONTAINER.has(me, ps, ! justCheck)) return false;
		if (Const.MATERIALS_DOOR.contains(material) && ! FPerm.DOOR.has(me, ps, ! justCheck)) return false;
		if (material == Material.STONE_BUTTON && ! FPerm.BUTTON.has(me, ps, ! justCheck)) return false;
		if (material == Material.LEVER && ! FPerm.LEVER.has(me, ps, ! justCheck)) return false;
		return true;
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();
		
		if (playerCanUseItemHere(player, PS.valueOf(block), event.getBucket(), false)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (playerCanUseItemHere(player, PS.valueOf(block), event.getBucket(), false)) return;
		
		event.setCancelled(true);
	}
	
	
}
