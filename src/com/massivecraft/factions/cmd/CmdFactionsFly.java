package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.type.TypeFaction;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.engine.EngineMoveChunk;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.MassiveException;
import com.massivecraft.massivecore.command.requirement.RequirementIsPlayer;
import com.massivecraft.massivecore.mixin.MixinTeleport;
import com.massivecraft.massivecore.mixin.TeleporterException;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.teleport.Destination;
import com.massivecraft.massivecore.teleport.DestinationSimple;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.factions.event.EventFactionsAbstractSender;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.mixin.MixinWorld;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.entity.EntityType;
import org.bukkit.command.CommandSender;

public class CmdFactionsFly
{
	public static boolean flyradius(MPlayer mplayer, Player player, Faction factionTo, Location locationHere, Faction factionHere, boolean verboose)
	{
		if ( ! MConf.get().flyEnabled)
		{
			mplayer.msg("<b>Sorry, the ability to fly is disabled on this server.");
			return false;
		}
				
		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if
		(
			MConf.get().flyAllowedEnemyDistance > 0
		)
		{
			World w = locationHere.getWorld();
			double x = locationHere.getX();
			double y = locationHere.getY();
			double z = locationHere.getZ();

			for (Player p : player.getServer().getOnlinePlayers())
			{
				
				if ( ! MPerm.getPermFly().hasfly(mplayer, factionHere, true)) continue;
				
				if (MUtil.isntPlayer(p)) continue;
								
				if (p == null || !p.isOnline() || p.isDead() || p == mplayer || p.getWorld() != w)
					continue;

				MPlayer fp = MPlayer.get(p);
				if (mplayer.getRelationTo(fp) != Rel.ENEMY)
					continue;

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = MConf.get().flyAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max)
					continue;

				mplayer.msg("<b>You cannot fly while an enemy is within %f blocks of you.", MConf.get().flyAllowedEnemyDistance);
				player.setAllowFlight(false);
				player.setFlying(false);
				return false;
			}
		}
	return true;
	}
}
