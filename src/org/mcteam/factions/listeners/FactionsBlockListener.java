package org.mcteam.factions.listeners;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.mcteam.factions.Board;
import org.mcteam.factions.Conf;
import org.mcteam.factions.FLocation;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.Faction;
import org.mcteam.factions.Factions;


public class FactionsBlockListener extends BlockListener {
	
	@Override
	public void onBlockPlace(BlockPlaceEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (!event.canBuild()) {
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
	
	public boolean playerCanBuildDestroyBlock(Player player, Block block, String action) {

		if (Conf.adminBypassPlayers.contains(player.getName())) {
			return true;
		}

		Faction otherFaction = Board.getFactionAt(new FLocation(block));
		
		if (otherFaction.isNone()) {
			return true;
		}
		
		FPlayer me = FPlayer.get(player);
		
		if (otherFaction.isSafeZone()) {
			if (Factions.hasPermManageSafeZone(player) || !Conf.safeZoneDenyBuild) {
				return true;
			}
			me.sendMessage("You can't "+action+" in a safe zone.");
			return false;
		}
		
		Faction myFaction = me.getFaction();
		
		// Cancel if we are not in our own territory
		if (myFaction != otherFaction) {
			me.sendMessage("You can't "+action+" in the territory of "+otherFaction.getTag(myFaction));
			return false;
		}
		
		return true;
	}
}
