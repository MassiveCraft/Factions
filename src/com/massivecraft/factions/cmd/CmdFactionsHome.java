package com.massivecraft.factions.cmd;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Perm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.cmd.req.ReqFactionsEnabled;
import com.massivecraft.factions.cmd.req.ReqHasFaction;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.UConf;
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
		
		this.addRequirements(ReqFactionsEnabled.get());
		this.addRequirements(ReqHasPerm.get(Perm.HOME.node));
		this.addRequirements(ReqHasFaction.get());
		this.addRequirements(ReqIsPlayer.get());
	}
	
	@Override
	public void perform()
	{
		UConf uconf = UConf.get(sender);
		
		// TODO: Hide this command on help also.
		if ( ! uconf.homesEnabled)
		{
			usender.msg("<b>对不起, 服务器禁止公会回城点功能.");
			return;
		}

		if ( ! uconf.homesTeleportCommandEnabled)
		{
			usender.msg("<b>对不起, 服务器禁止传送公会回城点功能.");
			return;
		}
		
		if ( ! usenderFaction.hasHome())
		{
			usender.msg("<b>你的公会还没有设置回城点. " + (usender.getRole().isLessThan(Rel.OFFICER) ? "<i> 请联系你的会长:" : "<i>You should:"));
			usender.sendMessage(Factions.get().getOuterCmdFactions().cmdFactionsSethome.getUseageTemplate());
			return;
		}
		
		if ( ! uconf.homesTeleportAllowedFromEnemyTerritory && usender.isInEnemyTerritory())
		{
			usender.msg("<b>当你在敌对阵营领地里时,禁止传送至公会回城点.");
			return;
		}
		
		if (!uconf.homesTeleportAllowedFromDifferentWorld && !me.getWorld().getName().equalsIgnoreCase(usenderFaction.getHome().getWorld()))
		{
			usender.msg("<b>当你在另一个世界时，禁止传送至公会回城点.");
			return;
		}
		
		
		Faction faction = BoardColls.get().getFactionAt(PS.valueOf(me));
		Location loc = me.getLocation().clone();
		
		// if player is not in a safe zone or their own faction territory, only allow teleport if no enemies are nearby
		if
		(
				uconf.homesTeleportAllowedEnemyDistance > 0
			&&
			faction.getFlag(FFlag.PVP)
			&&
			(
				! usender.isInOwnTerritory()
				||
				(
					usender.isInOwnTerritory()
					&&
					! uconf.homesTeleportIgnoreEnemiesIfInOwnTerritory
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
				if (usender.getRelationTo(fp) != Rel.ENEMY)
					continue;

				Location l = p.getLocation();
				double dx = Math.abs(x - l.getX());
				double dy = Math.abs(y - l.getY());
				double dz = Math.abs(z - l.getZ());
				double max = uconf.homesTeleportAllowedEnemyDistance;

				// box-shaped distance check
				if (dx > max || dy > max || dz > max)
					continue;

				usender.msg("<b>You cannot teleport to your faction home while an enemy is within " + uconf.homesTeleportAllowedEnemyDistance + " blocks of you.");
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
			Mixin.teleport(me, usenderFaction.getHome(), "your faction home", sender);
		}
		catch (TeleporterException e)
		{
			me.sendMessage(e.getMessage());
		}
	}
	
}
