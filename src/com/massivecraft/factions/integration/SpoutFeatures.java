package com.massivecraft.factions.integration;

import java.util.Set;

import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;

import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.SpoutManager;


public class SpoutFeatures
{
	private transient static AppearanceManager spoutApp;
	private transient static boolean spoutMe = false;
	private transient static SpoutMainListener mainListener;
	private transient static boolean listenersHooked;

	// set integration availability
	public static void setAvailable(boolean enable, String pluginName)
	{
		spoutMe = enable;
		if (spoutMe)
		{
			spoutApp = SpoutManager.getAppearanceManager();
			P.p.log("Found and will use features of "+pluginName);

			if (!listenersHooked)
			{
				listenersHooked = true;
				mainListener = new SpoutMainListener();
				P.p.getServer().getPluginManager().registerEvent(Event.Type.CUSTOM_EVENT, mainListener, Event.Priority.Normal, P.p);
			}
		}
		else
		{
			spoutApp = null;
		}
	}

	// If any Spout feature is enabled in conf.json, and we're successfully hooked into it
	public static boolean enabled()
	{
		return spoutMe && (
				   Conf.spoutFactionTagsOverNames
				|| Conf.spoutFactionTitlesOverNames
				|| Conf.spoutFactionAdminCapes
				|| Conf.spoutFactionModeratorCapes
				|| Conf.spoutTerritoryDisplayPosition > 0
				);
	}


	// update displayed current territory for specified player; returns false if unsuccessful
	public static boolean updateTerritoryDisplay(FPlayer player)
	{
		if (!enabled())
		{
			return false;
		}

		return mainListener.updateTerritoryDisplay(player);
	}

	public static void playerDisconnect(FPlayer player)
	{
		if (!enabled())
		{
			return;
		}

		mainListener.removeTerritoryLabel(player.getName());
	}


	// update all appearances between every player
	public static void updateAppearances()
	{
		if (!enabled())
		{
			return;
		}

		Set<FPlayer> players = FPlayers.i.getOnline();
		Faction factionA;

		for (FPlayer playerA : players)
		{
			factionA = playerA.getFaction();
			for (FPlayer playerB : players)
			{
				updateSingle(playerB.getPlayer(), playerA.getPlayer(), factionA.getRelationTo(playerB), factionA, playerA.getTitle(), playerA.getRole());
			}
		}
	}

	// update all appearances related to a specific player
	public static void updateAppearances(Player player)
	{
		if (!enabled() || player == null)
		{
			return;
		}

		Set<FPlayer> players = FPlayers.i.getOnline();
		FPlayer playerA = FPlayers.i.get(player);
		Faction factionA = playerA.getFaction();

		for (FPlayer playerB : players)
		{
			Player player2 = playerB.getPlayer();
			Relation rel = factionA.getRelationTo(playerB);
			updateSingle(player2, player, rel, factionA, playerA.getTitle(), playerA.getRole());
			updateSingle(player, player2, rel, playerB.getFaction(), playerB.getTitle(), playerB.getRole());
		}
	}

	// update all appearances related to a single faction
	public static void updateAppearances(Faction faction)
	{
		if (!enabled() || faction == null)
		{
			return;
		}

		Set<FPlayer> players = FPlayers.i.getOnline();
		Faction factionA, factionB;

		for (FPlayer playerA : players)
		{
			factionA = playerA.getFaction();

			for (FPlayer playerB : players)
			{
				factionB = playerB.getFaction();
				if (factionA != faction && factionB != faction)
				{
					continue;
				}
				updateSingle(playerB.getPlayer(), playerA.getPlayer(), factionA.getRelationTo(factionB), factionA, playerA.getTitle(), playerA.getRole());
			}
		}
	}

	// update all appearances between two factions
	public static void updateAppearances(Faction factionA, Faction factionB)
	{
		if (!enabled() || factionA == null || factionB == null)
		{
			return;
		}

		for (FPlayer playerA : factionA.getFPlayersWhereOnline(true))
		{
			for (FPlayer playerB : factionB.getFPlayersWhereOnline(true))
			{
				Player player1 = playerA.getPlayer();
				Player player2 = playerB.getPlayer();
				Relation rel = factionA.getRelationTo(factionB);
				updateSingle(player2, player1, rel, factionA, playerA.getTitle(), playerA.getRole());
				updateSingle(player1, player2, rel, factionB, playerB.getTitle(), playerB.getRole());
			}
		}
	}


	// update a single appearance; internal use only by above public methods
	private static void updateSingle(Player viewer, Player viewed, Relation relation, Faction viewedFaction, String viewedTitle, Role viewedRole)
	{
		if (viewer == null || viewed == null)
		{
			return;
		}

		SpoutPlayer sPlayer = SpoutManager.getPlayer(viewer);

		if (Conf.spoutFactionTagsOverNames || Conf.spoutFactionTitlesOverNames)
		{
			if (viewedFaction.isNormal())
			{
				String addTag = "";
				if (Conf.spoutFactionTagsOverNames)
				{
					addTag += viewedFaction.getTag(relation.getColor().toString() + "[") + "]";
				}
				String rolePrefix = viewedRole.getPrefix();
				if (Conf.spoutFactionTitlesOverNames && (!viewedTitle.isEmpty() || !rolePrefix.isEmpty()))
				{
					addTag += (addTag.isEmpty() ? "" : " ") + viewedRole.getPrefix() + viewedTitle;
				}
				spoutApp.setPlayerTitle(sPlayer, viewed, addTag + "\n" + viewed.getDisplayName());
			}
			else
			{
				spoutApp.setPlayerTitle(sPlayer, viewed, viewed.getDisplayName());
			}
		}

		if
		(
			(
				Conf.spoutFactionAdminCapes
				&&
				viewedRole.equals(Role.ADMIN)
			)
			|| 
			(
				Conf.spoutFactionModeratorCapes
				&&
				viewedRole.equals(Role.MODERATOR)
			)
		)
		{
			String cape = "";
			if (!viewedFaction.isNormal())
			{
				// yeah, no cape if no faction
			}
			else if (viewedFaction.isPeaceful())
			{
				cape = Conf.capePeaceful;
			}
			else if (relation.isNeutral())
			{
				cape = Conf.capeNeutral;
			}
			else if (relation.isMember())
			{
				cape = Conf.capeMember;
			}
			else if (relation.isEnemy())
			{
				cape = Conf.capeEnemy;
			}
			else if (relation.isAlly())
			{
				cape = Conf.capeAlly;
			}

			if (cape.isEmpty())
			{
				spoutApp.resetPlayerCloak(sPlayer, viewed);
			}
			else
			{
				spoutApp.setPlayerCloak(sPlayer, viewed, cape);
			}
		}
		else if (Conf.spoutFactionAdminCapes || Conf.spoutFactionModeratorCapes)
		{
			spoutApp.resetPlayerCloak(sPlayer, viewed);
		}
	}

	// method to convert a Bukkit ChatColor to a Spout Color
	protected static Color getSpoutColor(ChatColor inColor, int alpha)
	{
		if (inColor == null)
		{
			return new Color(191, 191, 191, alpha);
		}
		switch (inColor.getCode())
		{
			case 0x1:	return new Color(0, 0, 191, alpha);
			case 0x2:	return new Color(0, 191, 0, alpha);
			case 0x3:	return new Color(0, 191, 191, alpha);
			case 0x4:	return new Color(191, 0, 0, alpha);
			case 0x5:	return new Color(191, 0, 191, alpha);
			case 0x6:	return new Color(191, 191, 0, alpha);
			case 0x7:	return new Color(191, 191, 191, alpha);
			case 0x8:	return new Color(64, 64, 64, alpha);
			case 0x9:	return new Color(64, 64, 255, alpha);
			case 0xA:	return new Color(64, 255, 64, alpha);
			case 0xB:	return new Color(64, 255, 255, alpha);
			case 0xC:	return new Color(255, 64, 64, alpha);
			case 0xD:	return new Color(255, 64, 255, alpha);
			case 0xE:	return new Color(255, 255, 64, alpha);
			case 0xF:	return new Color(255, 255, 255, alpha);
			default:	return new Color(0, 0, 0, alpha);
		}
	}
}
