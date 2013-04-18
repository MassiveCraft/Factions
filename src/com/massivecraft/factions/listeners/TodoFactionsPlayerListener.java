package com.massivecraft.factions.listeners;

import java.util.Collection;
import java.util.Iterator;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.Const;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.mcore.event.MCorePlayerLeaveEvent;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.util.MUtil;


public class TodoFactionsPlayerListener implements Listener
{
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// If a player is joining the server ...
		Player player = event.getPlayer();
		FPlayer fplayer = FPlayerColl.get().get(player);
		
		// ... recalculate their power as if they were offline since last recalculation ...
		fplayer.recalculatePower(false);
		
		// ... update the current chunk ...
		fplayer.setCurrentChunk(PS.valueOf(event.getPlayer()));
		
		// ... notify the player about where they are ...
		if ( ! SpoutFeatures.updateTerritoryDisplay(fplayer))
		{
			fplayer.sendFactionHereMessage();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerLeave(MCorePlayerLeaveEvent event)
	{
		Player player = event.getPlayer();
		FPlayer fplayer = FPlayerColl.get().get(player);

		// Recalculate the power before the player leaves.
		// This is required since we recalculate as if the player were offline when they log back in.
		// TODO: When I setup universes I must do this for all universe instance of the player that logs off!
		fplayer.recalculatePower(true);

		SpoutFeatures.playerDisconnect(fplayer);
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMove(PlayerMoveEvent event)
	{
		// If the player is moving from one chunk to another ...
		if (MUtil.isSameChunk(event)) return;

		// ... update the stored current chunk ...
		Player player = event.getPlayer();
		FPlayer fplayer = FPlayerColl.get().get(player);
		
		PS chunkFrom = fplayer.getCurrentChunk();
		PS chunkTo = PS.valueOf(event.getTo()).getChunk(true);
		
		fplayer.setCurrentChunk(chunkTo);
		
		// ... TODO: assorted and uncleaned code below ...
		
		TerritoryAccess access = BoardColl.get().getTerritoryAccessAt(chunkTo);

		// Did we change "host"(faction)?
		boolean changedFaction = (BoardColl.get().getFactionAt(chunkFrom) != access.getHostFaction());

		// let Spout handle most of this if it's available
		boolean handledBySpout = changedFaction && SpoutFeatures.updateTerritoryDisplay(fplayer);
		
		if (fplayer.isMapAutoUpdating())
		{
			fplayer.sendMessage(BoardColl.get().getMap(fplayer.getFaction(), chunkTo, player.getLocation().getYaw()));
		}
		else if (changedFaction && ! handledBySpout)
		{
			fplayer.sendFactionHereMessage();
		}

		// show access info message if needed
		if ( ! handledBySpout && ! SpoutFeatures.updateAccessInfo(fplayer) && ! access.isDefault())
		{
			if (access.subjectHasAccess(fplayer))
				fplayer.msg("<g>You have access to this area.");
			else if (access.subjectAccessIsRestricted(fplayer))
				fplayer.msg("<b>This area has restricted access.");
		}

		if (fplayer.getAutoClaimFor() != null)
		{
			fplayer.attemptClaim(fplayer.getAutoClaimFor(), event.getTo(), true);
		}
	}

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

		if ( ! playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false))
		{
			event.setCancelled(true);
			return;
		}
	}


	// TODO: Refactor ! justCheck    -> to informIfNot
	// TODO: Possibly incorporate pain build... 
	public static boolean playerCanUseItemHere(Player player, Location loc, Material material, boolean justCheck)
	{
		String name = player.getName();
		if (ConfServer.playersWhoBypassAllProtection.contains(name)) return true;

		FPlayer me = FPlayerColl.get().get(name);
		if (me.isUsingAdminMode()) return true;
		if (Const.MATERIALS_EDIT_TOOLS.contains(material) && ! FPerm.BUILD.has(me, loc, ! justCheck)) return false;
		return true;
	}
	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck)
	{
		String name = player.getName();
		if (ConfServer.playersWhoBypassAllProtection.contains(name)) return true;

		FPlayer me = FPlayerColl.get().get(name);
		if (me.isUsingAdminMode()) return true;
		Location loc = block.getLocation();
		Material material = block.getType();
		
		if (Const.MATERIALS_EDIT_ON_INTERACT.contains(material) && ! FPerm.BUILD.has(me, loc, ! justCheck)) return false;
		if (Const.MATERIALS_CONTAINER.contains(material) && ! FPerm.CONTAINER.has(me, loc, ! justCheck)) return false;
		if (Const.MATERIALS_DOOR.contains(material)      && ! FPerm.DOOR.has(me, loc, ! justCheck)) return false;
		if (material == Material.STONE_BUTTON          && ! FPerm.BUTTON.has(me, loc, ! justCheck)) return false;
		if (material == Material.LEVER                 && ! FPerm.LEVER.has(me, loc, ! justCheck)) return false;
		return true;
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if ( ! playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false))
		{
			event.setCancelled(true);
			return;
		}
	}
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if ( ! playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false))
		{
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		// Get the player
		Player player = event.getPlayer();
		FPlayer me = FPlayerColl.get().get(player);
		
		// With adminmode no commands are denied. 
		if (me.isUsingAdminMode()) return;
		
		// The full command is converted to lowercase and does include the slash in the front
		String fullCmd = event.getMessage().toLowerCase();
		
		if (me.hasFaction() && me.getFaction().getFlag(FFlag.PERMANENT) && isCommandInList(fullCmd, ConfServer.permanentFactionMemberDenyCommands))
		{
			me.msg("<b>You can't use the command \""+fullCmd+"\" because you are in a permanent faction.");
			event.setCancelled(true);
			return;
		}
		
		Rel rel = me.getRelationToLocation();
		if (BoardColl.get().getFactionAt(me.getCurrentChunk()).isNone()) return;
		
		if (rel == Rel.NEUTRAL && isCommandInList(fullCmd, ConfServer.territoryNeutralDenyCommands))
		{
			me.msg("<b>You can't use the command \""+fullCmd+"\" in neutral territory.");
			event.setCancelled(true);
			return;
		}

		if (rel == Rel.ENEMY && isCommandInList(fullCmd, ConfServer.territoryEnemyDenyCommands))
		{
			me.msg("<b>You can't use the command \""+fullCmd+"\" in enemy territory.");
			event.setCancelled(true);
			return;
		}

		return;
	}

	private static boolean isCommandInList(String fullCmd, Collection<String> strings)
	{
		String shortCmd = fullCmd.substring(1);
		Iterator<String> iter = strings.iterator();
		while (iter.hasNext())
		{
			String cmdCheck = iter.next();
			if (cmdCheck == null)
			{
				iter.remove();
				continue;
			}
			cmdCheck = cmdCheck.toLowerCase();
			if (fullCmd.startsWith(cmdCheck)) return true;
			if (shortCmd.startsWith(cmdCheck)) return true;
		}
		return false;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event)
	{
		FPlayer badGuy = FPlayerColl.get().get(event.getPlayer());
		if (badGuy == null)
		{
			return;
		}

		SpoutFeatures.playerDisconnect(badGuy);

		// if player was banned (not just kicked), get rid of their stored info
		if (ConfServer.removePlayerDataWhenBanned && event.getReason().equals("Banned by admin."))
		{
			if (badGuy.getRole() == Rel.LEADER)
				badGuy.getFaction().promoteNewLeader();
			badGuy.leave(false);
			badGuy.detach();
		}
	}
	
	// -------------------------------------------- //
	// VisualizeUtil
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveClearVisualizations(PlayerMoveEvent event)
	{
		Block blockFrom = event.getFrom().getBlock();
		Block blockTo = event.getTo().getBlock();
		if (blockFrom.equals(blockTo)) return;
		
		VisualizeUtil.clear(event.getPlayer());
	}
}
