package com.massivecraft.factions.engine;

import java.util.Collections;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerMoveEvent;

import com.massivecraft.factions.Const;
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

		// ... and send info onwards.
		this.moveChunkTerritoryInfo(mplayer, player, chunkFrom, chunkTo, factionFrom, factionTo);
		this.moveChunkAutoClaim(mplayer, chunkTo);
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

}
