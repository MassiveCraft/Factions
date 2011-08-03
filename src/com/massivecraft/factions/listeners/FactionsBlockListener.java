package com.massivecraft.factions.listeners;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;


public class FactionsBlockListener extends BlockListener {
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!event.canBuild()) {
			return;
		}
		// special case for flint&steel, which should only be prevented by DenyUsage list
		if (event.getBlockPlaced().getType() == Material.FIRE) {
			return;
		}

		if ( ! this.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "build")) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockBreak(BlockBreakEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if ( ! this.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "destroy")) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockDamage(BlockDamageEvent event) {
		if (event.isCancelled()) {
			return;
		}

		if (event.getInstaBreak() && ! this.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock(), "destroy")) {
			event.setCancelled(true);
		}
	}

	@Override
	public void onBlockPistonExtend(BlockPistonExtendEvent event) {
		if (event.isCancelled() || !Conf.pistonProtectionThroughDenyBuild) {
			return;
		}

		Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));

		// target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
		Block targetBlock = event.getBlock().getRelative(event.getDirection(), event.getLength() + 1);

		// if potentially pushing into air in another territory, we need to check it out
		if (targetBlock.isEmpty() && !canPistonMoveBlock(pistonFaction, targetBlock.getLocation())) {
			event.setCancelled(true);
			return;
		}

		/*
		 * note that I originally was testing the territory of each affected block, but since I found that pistons can only push
		 * up to 12 blocks and the width of any territory is 16 blocks, it should be safe (and much more lightweight) to test
		 * only the final target block as done above
		 */
	}

	@Override
	public void onBlockPistonRetract(BlockPistonRetractEvent event) {
		// if not a sticky piston, retraction should be fine
		if (event.isCancelled() || !event.isSticky() || !Conf.pistonProtectionThroughDenyBuild) {
			return;
		}

		Location targetLoc = event.getRetractLocation();

		// if potentially retracted block is just air, no worries
		if (targetLoc.getBlock().isEmpty()) {
			return;
		}

		Faction pistonFaction = Board.getFactionAt(new FLocation(event.getBlock()));

		if (!canPistonMoveBlock(pistonFaction, targetLoc)) {
			event.setCancelled(true);
			return;
		}
	}

	private boolean canPistonMoveBlock(Faction pistonFaction, Location target) {

		Faction otherFaction = Board.getFactionAt(new FLocation(target));

		if (pistonFaction == otherFaction) {
			return true;
		}

		if (otherFaction.isNone()) {
			if (!Conf.wildernessDenyBuild) {
				return true;
			}
			return false;
		}
		else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyBuild) {
				return true;
			}
			return false;
		}
		else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyBuild) {
				return true;
			}
			return false;
		}

		boolean areEnemies = pistonFaction.getRelation(otherFaction).isEnemy();
		boolean online = otherFaction.hasPlayersOnline();

		if (
			   (online && (areEnemies ? Conf.territoryEnemyDenyBuild : Conf.territoryDenyBuild))
			|| (!online && (areEnemies ? Conf.territoryEnemyDenyBuildWhenOffline : Conf.territoryDenyBuildWhenOffline))
			) {
			return false;
		}

		return true;
	}

	public boolean playerCanBuildDestroyBlock(Player player, Block block, String action) {

		if (Conf.adminBypassPlayers.contains(player.getName())) {
			return true;
		}

		FLocation loc = new FLocation(block);
		Faction otherFaction = Board.getFactionAt(loc);
		FPlayer me = FPlayer.get(player);

		if (otherFaction.isNone()) {
			if (!Conf.wildernessDenyBuild || Factions.hasPermAdminBypass(player)) {
				return true; // This is not faction territory. Use whatever you like here.
			}
			me.sendMessage("You can't "+action+" in the wilderness.");
			return false;
		}
		else if (otherFaction.isSafeZone()) {
			if (!Conf.safeZoneDenyBuild || Factions.hasPermManageSafeZone(player)) {
				return true;
			}
			me.sendMessage("You can't "+action+" in a safe zone.");
			return false;
		}
		else if (otherFaction.isWarZone()) {
			if (!Conf.warZoneDenyBuild || Factions.hasPermManageWarZone(player)) {
				return true;
			}
			me.sendMessage("You can't "+action+" in a war zone.");
			return false;
		}

		Faction myFaction = me.getFaction();
		boolean areEnemies = myFaction.getRelation(otherFaction).isEnemy();

		// Cancel if we are not in our own territory
		if (myFaction != otherFaction) {
			boolean online = otherFaction.hasPlayersOnline();
			if (
				   (online && (areEnemies ? Conf.territoryEnemyDenyBuild : Conf.territoryDenyBuild))
				|| (!online && (areEnemies ? Conf.territoryEnemyDenyBuildWhenOffline : Conf.territoryDenyBuildWhenOffline))
				) {
				me.sendMessage("You can't "+action+" in the territory of "+otherFaction.getTag(myFaction));
				return false;
			}
			//added by Bladedpenguin@gmail.com
			//if not denybuild, hurt the player for building?
			else if (
					   (online && (areEnemies ? Conf.territoryEnemyPainBuild : Conf.territoryPainBuild))
					|| (!online && (areEnemies ? Conf.territoryEnemyPainBuildWhenOffline : Conf.territoryPainBuildWhenOffline))
					) {
					me.sendMessage("You are hurt for "+action+" in the territory of "+otherFaction.getTag(myFaction));
					player.damage(1);
				}
		}
		// Also cancel if player doesn't have ownership rights for this claim
		// Also restructured by bladedpenguin/locutus
		else if (
			   Conf.ownedAreasEnabled
			&& !myFaction.playerHasOwnershipRights(me, loc)
			&& !Factions.hasPermOwnershipBypass(player)
			) {
			if (Conf.ownedAreaDenyBuild){
				me.sendMessage("You can't "+action+" in this territory, it is owned by: "+myFaction.getOwnerListString(loc));
				return false;
			} else if (Conf.ownedAreaPainBuild){
				me.sendMessage("You are hurt for "+action+" in this territory, it is owned by: "+myFaction.getOwnerListString(loc));
				player.damage(1);
			}
		}
		
		return true;
	}
}
