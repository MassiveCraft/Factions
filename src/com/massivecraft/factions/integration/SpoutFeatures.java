package com.massivecraft.factions.integration;

import java.util.Set;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.P;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

import com.massivecraft.factions.struct.Rel;
import com.massivecraft.factions.util.HealthBarUtil;

import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.SpoutManager;


public class SpoutFeatures
{
	private transient static boolean spoutMe = false;
	private transient static SpoutMainListener mainListener;
	private transient static boolean listenersHooked;

	public static void setup()
	{
		Plugin test = Bukkit.getServer().getPluginManager().getPlugin("Spout");
		if (test == null || !test.isEnabled()) return;

		setAvailable(true, test.getDescription().getFullName());
	}

	// set integration availability
	public static void setAvailable(boolean enable, String pluginName)
	{
		spoutMe = enable;
		if (!spoutMe) return;

		P.p.log("Found and will use features of "+pluginName);

		if (!listenersHooked)
		{
			listenersHooked = true;
			mainListener = new SpoutMainListener();
			Bukkit.getServer().getPluginManager().registerEvents(mainListener, P.p);
		}
	}

	// If we're successfully hooked into Spout
	public static boolean enabled()
	{
		return spoutMe;
	}

	// If Spout is available and the specified Player is running the Spoutcraft client
	public static boolean availableFor(Player player)
	{
		return spoutMe && SpoutManager.getPlayer(player).isSpoutCraftEnabled();
	}


	// update displayed current territory for all players inside a specified chunk; if specified chunk is null, then simply update everyone online
	public static void updateTerritoryDisplayLoc(FLocation fLoc)
	{
		if (!enabled())
			return;

		Set<FPlayer> players = FPlayers.i.getOnline();

		for (FPlayer player : players)
		{
			if (fLoc == null)
				mainListener.updateTerritoryDisplay(player, false);
			else if (player.getLastStoodAt().equals(fLoc))
				mainListener.updateTerritoryDisplay(player, true);
		}
	}

	// update displayed current territory for specified player; returns false if unsuccessful
	public static boolean updateTerritoryDisplay(FPlayer player)
	{
		if (!enabled())
			return false;

		return mainListener.updateTerritoryDisplay(player, true);
	}

	public static void playerDisconnect(FPlayer player)
	{
		if (!enabled())
			return;

		mainListener.removeTerritoryLabels(player.getName());
	}


	// update all appearances between every player
	public static void updateAppearances()
	{
		if (!enabled())
			return;

		Set<FPlayer> players = FPlayers.i.getOnline();

		for (FPlayer playerA : players)
		{
			for (FPlayer playerB : players)
			{
				updateSingle(playerB, playerA);
			}
		}
	}

	// update all appearances related to a specific player
	public static void updateAppearances(Player player)
	{
		if (!enabled() || player == null)
			return;

		Set<FPlayer> players = FPlayers.i.getOnline();
		FPlayer playerA = FPlayers.i.get(player);

		for (FPlayer playerB : players)
		{
			updateSingle(playerB, playerA);
			updateSingle(playerA, playerB);
		}
	}
	
	// update how this player looks in the eyes of all other players
	public static void updateMyAppearance(Player player)
	{
		if (!enabled() || player == null) return;

		Set<FPlayer> players = FPlayers.i.getOnline();
		FPlayer playerA = FPlayers.i.get(player);

		for (FPlayer playerB : players)
		{
			updateSingle(playerB, playerA);
		}
	}

	// as above method, but with a delay added; useful for after-login update which doesn't always propagate if done immediately
	public static void updateAppearancesShortly(final Player player)
	{
		P.p.getServer().getScheduler().scheduleSyncDelayedTask(P.p, new Runnable()
		{
			@Override
			public void run()
			{
				updateAppearances(player);
			}
		}, 100);
	}

	// update all appearances related to a single faction
	public static void updateAppearances(Faction faction)
	{
		if (!enabled() || faction == null)
			return;

		Set<FPlayer> players = FPlayers.i.getOnline();
		Faction factionA;

		for (FPlayer playerA : players)
		{
			factionA = playerA.getFaction();

			for (FPlayer playerB : players)
			{
				if (factionA != faction && playerB.getFaction() != faction)
					continue;

				updateSingle(playerB, playerA);
			}
		}
	}

	// update all appearances between two factions
	public static void updateAppearances(Faction factionA, Faction factionB)
	{
		if (!enabled() || factionA == null || factionB == null)
			return;

		for (FPlayer playerA : factionA.getFPlayersWhereOnline(true))
		{
			for (FPlayer playerB : factionB.getFPlayersWhereOnline(true))
			{
				updateSingle(playerB, playerA);
				updateSingle(playerA, playerB);
			}
		}
	}


	// update a single appearance; internal use only by above public methods
	private static void updateSingle(FPlayer viewer, FPlayer viewed)
	{
		if (viewer == null || viewed == null)
			return;

		Faction viewedFaction = viewed.getFaction();
		if (viewedFaction == null)
			return;

		// these still end up returning null on occasion at this point, mucking up the SpoutManager.getPlayer() method
		if (viewer.getPlayer() == null || viewed.getPlayer() == null)
			return;

		SpoutPlayer pViewer = SpoutManager.getPlayer(viewer.getPlayer());
		SpoutPlayer pViewed = SpoutManager.getPlayer(viewed.getPlayer());
		if (pViewed == null || pViewer == null)
			return;

		String viewedTitle = viewed.getTitle();
		Rel viewedRole = viewed.getRole();

		if ((Conf.spoutFactionTagsOverNames || Conf.spoutFactionTitlesOverNames || Conf.spoutHealthBarUnderNames) && viewer != viewed)
		{
			String title = pViewed.getDisplayName();
			
			if (viewedFaction.isNormal())
			{
				String addTag = "";
				if (Conf.spoutFactionTagsOverNames)
				{
					addTag += viewedFaction.getTag(viewed.getColorTo(viewer).toString() + "[") + "]";
				}
					
				String rolePrefix = viewedRole.getPrefix();
				if (Conf.spoutFactionTitlesOverNames && (!viewedTitle.isEmpty() || !rolePrefix.isEmpty()))
				{
					addTag += (addTag.isEmpty() ? "" : " ") + viewedRole.getPrefix() + viewedTitle;
				}

				title = addTag + "\n" + title;
			}
			
			if (Conf.spoutHealthBarUnderNames)
			{
				title += "\n";
				title += HealthBarUtil.getHealthbar(pViewed.getHealth() / 20d);
			}
			
			pViewed.setTitleFor(pViewer, title);
		}

		if
		(
			(
				Conf.spoutFactionLeaderCapes
				&&
				viewedRole.equals(Rel.LEADER)
			)
			|| 
			(
				Conf.spoutFactionOfficerCapes
				&&
				viewedRole.equals(Rel.OFFICER)
			)
		)
		{
			Rel relation = viewer.getRelationTo(viewed);
			String cape = "";
			if (!viewedFaction.isNormal())
			{
				// yeah, no cape if no faction
			}
			else if (relation == Rel.TRUCE)
				cape = Conf.capePeaceful;
			else if (relation == Rel.NEUTRAL)
				cape = Conf.capeNeutral;
			else if (relation == Rel.MEMBER)
				cape = Conf.capeMember;
			else if (relation == Rel.RECRUIT)
				cape = Conf.capeRecruit;
			else if (relation == Rel.ENEMY)
				cape = Conf.capeEnemy;
			else if (relation == Rel.ALLY)
				cape = Conf.capeAlly;

			if (cape.isEmpty())
				pViewed.resetCapeFor(pViewer);
			else
				pViewed.setCapeFor(pViewer, cape);
		}
		else if (Conf.spoutFactionLeaderCapes || Conf.spoutFactionOfficerCapes)
		{
			pViewed.resetCapeFor(pViewer);
		}
	}


	// method to convert a Bukkit ChatColor to a Spout Color
	protected static Color getSpoutColor(ChatColor inColor, int alpha)
	{
		if (inColor == null)
			return SpoutFixedColor(191, 191, 191, alpha);

		switch (inColor.getChar())
		{
			case 0x1:	return SpoutFixedColor(0, 0, 191, alpha);
			case 0x2:	return SpoutFixedColor(0, 191, 0, alpha);
			case 0x3:	return SpoutFixedColor(0, 191, 191, alpha);
			case 0x4:	return SpoutFixedColor(191, 0, 0, alpha);
			case 0x5:	return SpoutFixedColor(191, 0, 191, alpha);
			case 0x6:	return SpoutFixedColor(191, 191, 0, alpha);
			case 0x7:	return SpoutFixedColor(191, 191, 191, alpha);
			case 0x8:	return SpoutFixedColor(64, 64, 64, alpha);
			case 0x9:	return SpoutFixedColor(64, 64, 255, alpha);
			case 0xA:	return SpoutFixedColor(64, 255, 64, alpha);
			case 0xB:	return SpoutFixedColor(64, 255, 255, alpha);
			case 0xC:	return SpoutFixedColor(255, 64, 64, alpha);
			case 0xD:	return SpoutFixedColor(255, 64, 255, alpha);
			case 0xE:	return SpoutFixedColor(255, 255, 64, alpha);
			case 0xF:	return SpoutFixedColor(255, 255, 255, alpha);
			default:	return SpoutFixedColor(0, 0, 0, alpha);
		}
	}
	private static Color SpoutFixedColor(int r, int g, int b, int a)
	{
		return new Color(r/255.0f, g/255.0f, b/255.0f, a/255.0f);
	}
}
