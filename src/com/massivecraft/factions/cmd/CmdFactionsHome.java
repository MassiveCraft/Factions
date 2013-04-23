package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqRoleIsAtLeast;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.FactionsEventHomeTeleport;
import com.massivecraft.mcore.cmd.req.ReqHasPerm;
import com.massivecraft.mcore.cmd.req.ReqIsPlayer;
import com.massivecraft.mcore.mixin.Mixin;
import com.massivecraft.mcore.mixin.TeleporterException;
import com.massivecraft.mcore.ps.PS;


public class CmdFactionsHome extends FCommand
{
	public CmdFactionsHome()
	{
		this.addAliases("home");

		this.addRequirements(ReqHasPerm.get(Perm.HOME.node));
		this.addRequirements(ReqIsPlayer.get());
		this.addRequirements(ReqRoleIsAtLeast.get(Rel.RECRUIT));
	}

	@Override
	public void perform()
	{
		// TODO: Hide this command on help also.
		if ( ! ConfServer.homesEnabled)
		{
			fme.msg("<b>Sorry, Faction homes are disabled on this server.");
			return;
		}

		if ( ! ConfServer.homesTeleportCommandEnabled)
		{
			fme.msg("<b>Sorry, the ability to teleport to Faction homes is disabled on this server.");
			return;
		}

		if ( ! myFaction.hasHome())
		{
			fme.msg("<b>Your faction does not have a home. " + (fme.getRole().isLessThan(Rel.OFFICER) ? "<i> Ask your leader to:" : "<i>You should:"));
			fme.sendMessage(Factions.get().getOuterCmdFactions().cmdFactionsSethome.getUseageTemplate());
			return;
		}

		if ( ! ConfServer.homesTeleportAllowedFromEnemyTerritory && fme.isInEnemyTerritory())
		{
			fme.msg("<b>You cannot teleport to your faction home while in the territory of an enemy faction.");
			return;
		}

		if (!ConfServer.homesTeleportAllowedFromDifferentWorld && !me.getWorld().getName().equalsIgnoreCase(myFaction.getHome().getWorld()))
		{
			fme.msg("<b>You cannot teleport to your faction home while in a different world.");
			return;
		}


		Faction faction = BoardColls.get().getFactionAt(PS.valueOf(me));
		Location loc = me.getLocation().clone();

		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if
		(
			ConfServer.homesTeleportAllowedEnemyDistance > 0
			&&
			faction.getFlag(FFlag.PVP)
			&&
			(
				! fme.isInOwnTerritory()
				||
				(
					fme.isInOwnTerritory()
					&&
					! ConfServer.homesTeleportIgnoreEnemiesIfInOwnTerritory
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

				UPlayer fp = UPlayer.get(p);
				if (fme.getRelationTo(fp) != Rel.ENEMY)
					continue;

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = ConfServer.homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max)
					continue;

				fme.msg("<b>You cannot teleport to your faction home while an enemy is within " + ConfServer.homesTeleportAllowedEnemyDistance + " blocks of you.");
				return;
			}
		}

		// Event
		FactionsEventHomeTeleport event = new FactionsEventHomeTeleport(sender);
		event.run();
		if (event.isCancelled()) return;

		// Apply
		try
		{
			Mixin.teleport(me, myFaction.getHome(), "your faction home", sender);
		}
		catch (TeleporterException e)
		{
			me.sendMessage(e.getMessage());
		}
	}

}
