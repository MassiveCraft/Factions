package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsHomeTeleport;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.mixin.TeleporterException;
import com.massivecraft.massivecore.ps.PS;


public class CmdFactionsHome extends FCommand
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsHome()
	{
		// Aliases
		this.addAliases("home");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.HOME.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqIsPlayer.get());
	}

	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		// TODO: Hide this command on help also.
		if ( ! MConf.get().homesEnabled)
		{
			usender.msg("<b>Sorry, Faction homes are disabled on this server.");
			return;
		}

		if ( ! MConf.get().homesTeleportCommandEnabled)
		{
			usender.msg("<b>Sorry, the ability to teleport to Faction homes is disabled on this server.");
			return;
		}
		
		if ( ! usenderFaction.hasHome())
		{
			usender.msg("<b>Your faction does not have a home. " + (usender.getRole().isLessThan(Rel.OFFICER) ? "<i> Ask your leader to:" : "<i>You should:"));
			usender.sendMessage(Factions.get().getOuterCmdFactions().cmdFactionsSethome.getUseageTemplate());
			return;
		}
		
		if ( ! MConf.get().homesTeleportAllowedFromEnemyTerritory && usender.isInEnemyTerritory())
		{
			usender.msg("<b>You cannot teleport to your faction home while in the territory of an enemy faction.");
			return;
		}
		
		if ( ! MConf.get().homesTeleportAllowedFromDifferentWorld && !me.getWorld().getName().equalsIgnoreCase(usenderFaction.getHome().getWorld()))
		{
			usender.msg("<b>You cannot teleport to your faction home while in a different world.");
			return;
		}
		
		
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(me));
		Location loc = me.getLocation().clone();
		
		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if
		(
			MConf.get().homesTeleportAllowedEnemyDistance > 0
			&&
			faction.getFlag(FFlag.PVP)
			&&
			(
				! usender.isInOwnTerritory()
				||
				(
					usender.isInOwnTerritory()
					&&
					! MConf.get().homesTeleportIgnoreEnemiesIfInOwnTerritory
				)
			)
		)
		{
			World w = loc.getWorld();
			double x = loc.getX();
			double y = loc.getY();
			double z = loc.getZ();

			for (Player p : me.getServer().getOnlinePlayers())
			{
				if (p == null || !p.isOnline() || p.isDead() || p == me || p.getWorld() != w)
					continue;

				MPlayer fp = MPlayer.get(p);
				if (usender.getRelationTo(fp) != Rel.ENEMY)
					continue;

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = MConf.get().homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max)
					continue;

				usender.msg("<b>You cannot teleport to your faction home while an enemy is within " + MConf.get().homesTeleportAllowedEnemyDistance + " blocks of you.");
				return;
			}
		}

		// Event
		EventFactionsHomeTeleport event = new EventFactionsHomeTeleport(sender);
		event.run();
		if (event.isCancelled()) return;
		
		// Apply
		try
		{
			Mixin.teleport(me, usenderFaction.getHome(), "your faction home", sender);
		}
		catch (TeleporterException e)
		{
			me.sendMessage(e.getMessage());
		}
	}
	
}
