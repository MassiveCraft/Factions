package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.arg.ARFaction;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.event.EventFactionsHomeTeleport;
import com.massivecraft.massivecore.cmd.req.ReqHasPerm;
import com.massivecraft.massivecore.cmd.req.ReqIsPlayer;
import com.massivecraft.massivecore.mixin.Mixin;
import com.massivecraft.massivecore.mixin.TeleporterException;
import com.massivecraft.massivecore.ps.PS;


public class CmdFactionsHome extends FactionsCommandHome
{
	// -------------------------------------------- //
	// CONSTRUCT
	// -------------------------------------------- //
	
	public CmdFactionsHome()
	{
		// Aliases
		this.addAliases("home");
		
		// Args
		this.addOptionalArg("faction", "you");

		// Requirements
		this.addRequirements(ReqHasPerm.get(Perm.HOME.node));
		this.addRequirements(ReqIsPlayer.get());
	}
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public void perform()
	{
		if ( ! MConf.get().homesTeleportCommandEnabled)
		{
			msender.msg("<b>Sorry, the ability to teleport to Faction homes is disabled on this server.");
			return;
		}
		
		// Args
		Faction faction = this.arg(0, ARFaction.get(), msenderFaction);
		if (faction == null) return;
		PS home = faction.getHome();
		String homeDesc = "home for " + faction.describeTo(msender, false);
		
		// Any and MPerm
		if ( ! MPerm.getPermHome().has(msender, faction, true)) return;
		
		if (home == null)
		{
			msender.msg("<b>%s <b>does not have a home.", faction.describeTo(msender, true));
			
			if (MPerm.getPermSethome().has(msender, faction, false))
			{
				msender.msg("<i>You should:");
				msender.sendMessage(Factions.get().getOuterCmdFactions().cmdFactionsSethome.getUseageTemplate());
			}
			
			return;
		}
		
		if ( ! MConf.get().homesTeleportAllowedFromEnemyTerritory && msender.isInEnemyTerritory())
		{
			msender.msg("<b>You cannot teleport to %s <b>while in the territory of an enemy faction.", homeDesc);
			return;
		}
		
		if ( ! MConf.get().homesTeleportAllowedFromDifferentWorld && !me.getWorld().getName().equalsIgnoreCase(home.getWorld()))
		{
			msender.msg("<b>You cannot teleport to %s <b>while in a different world.", homeDesc);
			return;
		}
		
		
		Faction factionHere = BoardColl.get().getFactionAt(PS.valueOf(me.getLocation()));
		Location locationHere = me.getLocation().clone();
		
		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if
		(
			MConf.get().homesTeleportAllowedEnemyDistance > 0
			&&
			factionHere.getFlag(MFlag.getFlagPvp())
			&&
			(
				! msender.isInOwnTerritory()
				||
				(
					msender.isInOwnTerritory()
					&&
					! MConf.get().homesTeleportIgnoreEnemiesIfInOwnTerritory
				)
			)
		)
		{
			World w = locationHere.getWorld();
			double x = locationHere.getX();
			double y = locationHere.getY();
			double z = locationHere.getZ();

			for (Player p : me.getServer().getOnlinePlayers())
			{
				if (p == null || !p.isOnline() || p.isDead() || p == me || p.getWorld() != w)
					continue;

				MPlayer fp = MPlayer.get(p);
				if (msender.getRelationTo(fp) != Rel.ENEMY)
					continue;

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = MConf.get().homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max)
					continue;

				msender.msg("<b>You cannot teleport to %s <b>while an enemy is within %f blocks of you.", homeDesc, MConf.get().homesTeleportAllowedEnemyDistance);
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
			Mixin.teleport(me, home, homeDesc, sender);
		}
		catch (TeleporterException e)
		{
			me.sendMessage(e.getMessage());
		}
	}
	
}
