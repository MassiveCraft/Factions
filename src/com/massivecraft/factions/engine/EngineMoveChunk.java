package com.massivecraft.factions.engine;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.Location;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.player.PlayerToggleFlightEvent;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.cmd.CmdFactionsFly;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.Txt;
import com.massivecraft.factions.entity.MPerm;


public class EngineMoveChunk extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineMoveChunk i = new EngineMoveChunk();
	public static EngineMoveChunk get() { return i; }

	// -------------------------------------------- //
	// MOVE CHUNK: DETECT
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void moveChunkDetect(PlayerMoveEvent event)
	{
		// If the player is moving from one chunk to another ...
		if (MUtil.isSameChunk(event)) return;
		Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;

		// ... gather info on the player and the move ...
		MPlayer mplayer = MPlayer.get(player);

		PS chunkFrom = PS.valueOf(event.getFrom()).getChunk(true);
		PS chunkTo = PS.valueOf(event.getTo()).getChunk(true);

		Faction factionFrom = BoardColl.get().getFactionAt(chunkFrom);
		Faction factionTo = BoardColl.get().getFactionAt(chunkTo);
		Faction factionHere = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
		Location locationHere = player.getLocation().clone();
		Faction moveto = BoardColl.get().getFactionAt(PS.valueOf(event.getTo()));

		// ... and send info onwards.
		this.moveChunkTerritoryInfo(mplayer, player, chunkFrom, chunkTo, factionFrom, factionTo);
		this.moveChunkAutoClaim(mplayer, chunkTo);
		this.canFly(mplayer, player, factionTo, locationHere, factionHere);
	}

	// -------------------------------------------- //
	// MOVE CHUNK: TERRITORY INFO
	// -------------------------------------------- //

	public void moveChunkTerritoryInfo(MPlayer mplayer, Player player, PS chunkFrom, PS chunkTo, Faction factionFrom, Faction factionTo)
	{
		// send host faction info updates
		if (mplayer.isMapAutoUpdating())
		{
			List<Object> message = BoardColl.get().getMap(mplayer, chunkTo, player.getLocation().getYaw(), Const.MAP_WIDTH, Const.MAP_HEIGHT);
			mplayer.message(message);
		}
		else if (factionFrom != factionTo)
		{
			if (mplayer.isTerritoryInfoTitles())
			{
				String maintitle = parseTerritoryInfo(MConf.get().territoryInfoTitlesMain, mplayer, factionTo);
				String subtitle = parseTerritoryInfo(MConf.get().territoryInfoTitlesSub, mplayer, factionTo);
				MixinTitle.get().sendTitleMessage(player, MConf.get().territoryInfoTitlesTicksIn, MConf.get().territoryInfoTitlesTicksStay, MConf.get().territoryInfoTitleTicksOut, maintitle, subtitle);
			}
			else
			{
				String message = parseTerritoryInfo(MConf.get().territoryInfoChat, mplayer, factionTo);
				player.sendMessage(message);
			}
		}

		// Show access level message if it changed.
		TerritoryAccess accessFrom = BoardColl.get().getTerritoryAccessAt(chunkFrom);
		Boolean hasTerritoryAccessFrom = accessFrom.hasTerritoryAccess(mplayer);

		TerritoryAccess accessTo = BoardColl.get().getTerritoryAccessAt(chunkTo);
		Boolean hasTerritoryAccessTo = accessTo.hasTerritoryAccess(mplayer);

		if ( ! MUtil.equals(hasTerritoryAccessFrom, hasTerritoryAccessTo))
		{
			if (hasTerritoryAccessTo == null)
			{
				mplayer.msg("<i>You have standard access to this area.");
			}
			else if (hasTerritoryAccessTo)
			{
				mplayer.msg("<g>You have elevated access to this area.");
			}
			else
			{
				mplayer.msg("<b>You have decreased access to this area.");
			}
		}
	}

	public String parseTerritoryInfo(String string, MPlayer mplayer, Faction faction)
	{
		if (string == null) throw new NullPointerException("string");
		if (faction == null) throw new NullPointerException("faction");

		string = Txt.parse(string);

		string = string.replace("{name}", faction.getName());
		string = string.replace("{relcolor}", faction.getColorTo(mplayer).toString());
		string = string.replace("{desc}", faction.getDescription());

		return string;
	}

	// -------------------------------------------- //
	// MOVE CHUNK: AUTO CLAIM
	// -------------------------------------------- //

	public void moveChunkAutoClaim(MPlayer mplayer, PS chunkTo)
	{
		// If the player is auto claiming ...
		Faction autoClaimFaction = mplayer.getAutoClaimFaction();
		if (autoClaimFaction == null) return;

		// ... try claim.
		mplayer.tryClaim(autoClaimFaction, Collections.singletonList(chunkTo));
	}
		
	public void canFly(MPlayer mplayer, Player player, Faction factionTo, Location locationHere, Faction factionHere)
	{		
		if ( ! CmdFactionsFly.flyradius(mplayer, player, factionHere, locationHere, factionTo, true)) return;
	}
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void flyCheckOnMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;

		// ... gather info on the player and the move ...
		MPlayer mplayer = MPlayer.get(player);

		PS chunkFrom = PS.valueOf(event.getFrom()).getChunk(true);
		PS chunkTo = PS.valueOf(event.getTo()).getChunk(true);

		Faction factionFrom = BoardColl.get().getFactionAt(chunkFrom);
		Faction factionTo = BoardColl.get().getFactionAt(chunkTo);
		Faction factionHere = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
		Location locationHere = player.getLocation().clone();

		if (player.getGameMode() == GameMode.CREATIVE) return;
		
		if (mplayer.isOverriding())
		{
			player.setAllowFlight(true);
			return;
		}
				
		PS ps = PS.valueOf(player.getLocation()).getChunk(true);
			
		if (MPerm.getPermFly().hasfly(mplayer, factionTo, true))
		{
			if ( ! CmdFactionsFly.flyradius(mplayer, player, factionHere, locationHere, factionTo, true)) return;
			
			player.setAllowFlight(true);
			return;
		}
		else
		{
			player.setAllowFlight(false);
			player.setFlying(false);
			return;
		}
	}
	
}
