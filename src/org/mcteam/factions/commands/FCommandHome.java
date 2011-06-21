package org.mcteam.factions.commands;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.mcteam.factions.Board;
import org.mcteam.factions.Conf;
import org.mcteam.factions.Faction;
import org.mcteam.factions.FLocation;
import org.mcteam.factions.FPlayer;
import org.mcteam.factions.struct.Relation;
import org.mcteam.factions.struct.Role;

public class FCommandHome extends FBaseCommand {
	
	public FCommandHome() {
		aliases.add("home");
		
		helpDescription = "Teleport to the faction home";
	}
	
	@Override
	public void perform() {
		if ( ! assertHasFaction()) {
			return;
		}
		
		if ( ! Conf.homesEnabled) {
			me.sendMessage("Sorry, Faction homes are disabled on this server.");
			return;
		}
		
		Faction myFaction = me.getFaction();
		
		if ( ! myFaction.hasHome()) {
			me.sendMessage("You faction does not have a home. " + (me.getRole().value < Role.MODERATOR.value ? " Ask your leader to:" : "You should:"));
			me.sendMessage(new FCommandSethome().getUseageTemplate());
			return;
		}
		
		Faction faction = Board.getFactionAt(new FLocation(player.getLocation()));
		
		if (!Conf.homesTeleportAllowedFromEnemyTerritory && me.isInEnemyTerritory()) {
			me.sendMessage("You cannot teleport to your faction home while in the territory of an enemy faction.");
			return;
		}
		
		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if (Conf.homesTeleportAllowedEnemyDistance > 0 && ! faction.isSafeZone() && ! me.isInOwnTerritory()) {
			Location loc = player.getLocation();
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for (Player p : player.getServer().getOnlinePlayers())
			{
				if (p == null || !p.isOnline() || p.isDead() || p == player || p.getWorld() != w)
					continue;

				FPlayer fp = FPlayer.get(p);
				if (me.getRelation(fp) != Relation.ENEMY)
					continue;

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = Conf.homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max)
					continue;

				me.sendMessage("You cannot teleport to your faction home while an enemy is within " + Conf.homesTeleportAllowedEnemyDistance + " blocks of you.");
				return;
			}
		}
		
		player.teleport(myFaction.getHome());
	}
	
}
