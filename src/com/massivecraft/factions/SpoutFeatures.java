package com.massivecraft.factions;

import java.util.Set;

import org.bukkit.plugin.Plugin;
import org.bukkit.entity.Player;

import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.struct.Role;

import org.getspout.spoutapi.player.AppearanceManager;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.SpoutManager;


public class SpoutFeatures {
	private transient static AppearanceManager spoutApp;
	private transient static boolean spoutMe = false;

	public static void setup(Factions factions) {
		Plugin test = factions.getServer().getPluginManager().getPlugin("Spout");

		if (test != null && test.isEnabled()) {
			setAvailable(true, test.getDescription().getFullName());
		}
		else {
			setAvailable(false, "");
		}
	}

	// set integration availability
	public static void setAvailable(boolean enable, String pluginName) {
		spoutMe = enable;
		if (spoutMe) {
			spoutApp = SpoutManager.getAppearanceManager();
			Factions.log("Found and will use features of "+pluginName);
		}
		else {
			spoutApp = null;
		}
	}

	// If any Spout feature is enabled in conf.json, and we're successfully hooked into it
	public static boolean enabled() {
		return spoutMe && (
				   Conf.spoutFactionTagsOverNames
				|| Conf.spoutFactionTitlesOverNames
				|| Conf.spoutFactionAdminCapes
				|| Conf.spoutFactionModeratorCapes
				);
	}


	// update all appearances between every player
	public static void updateAppearances() {
		if (!enabled()) {
			return;
		}

		Set<FPlayer> players = FPlayer.getAllOnline();
		Faction factionA;

		for (FPlayer playerA : players) {
			factionA = playerA.getFaction();
			for (FPlayer playerB : players) {
				updateSingle(playerB.getPlayer(), playerA.getPlayer(), factionA.getRelation(playerB), factionA, playerA.getTitle(), playerA.getRole());
			}
		}
	}

	// update all appearances related to a specific player
	public static void updateAppearances(Player player) {
		if (!enabled() || player == null) {
			return;
		}

		Set<FPlayer> players = FPlayer.getAllOnline();
		FPlayer playerA = FPlayer.get(player);
		Faction factionA = playerA.getFaction();

		for (FPlayer playerB : players) {
			Player player2 = playerB.getPlayer();
			Relation rel = factionA.getRelation(playerB);
			updateSingle(player2, player, rel, factionA, playerA.getTitle(), playerA.getRole());
			updateSingle(player, player2, rel, playerB.getFaction(), playerB.getTitle(), playerB.getRole());
		}
	}

	// update all appearances related to a single faction
	public static void updateAppearances(Faction faction) {
		if (!enabled() || faction == null) {
			return;
		}

		Set<FPlayer> players = FPlayer.getAllOnline();
		Faction factionA, factionB;

		for (FPlayer playerA : players) {
			factionA = playerA.getFaction();

			for (FPlayer playerB : players) {
				factionB = playerB.getFaction();
				if (factionA != faction && factionB != faction) {
					continue;
				}
				updateSingle(playerB.getPlayer(), playerA.getPlayer(), factionA.getRelation(factionB), factionA, playerA.getTitle(), playerA.getRole());
			}
		}
	}

	// update all appearances between two factions
	public static void updateAppearances(Faction factionA, Faction factionB) {
		if (!enabled() || factionA == null || factionB == null) {
			return;
		}

		for (FPlayer playerA : factionA.getFPlayersWhereOnline(true)) {
			for (FPlayer playerB : factionB.getFPlayersWhereOnline(true)) {
				Player player1 = playerA.getPlayer();
				Player player2 = playerB.getPlayer();
				Relation rel = factionA.getRelation(factionB);
				updateSingle(player2, player1, rel, factionA, playerA.getTitle(), playerA.getRole());
				updateSingle(player1, player2, rel, factionB, playerB.getTitle(), playerB.getRole());
			}
		}
	}


	// update a single appearance; internal use only by above public methods
	private static void updateSingle(Player viewer, Player viewed, Relation relation, Faction viewedFaction, String viewedTitle, Role viewedRole) {
		if (viewer == null || viewed == null) {
			return;
		}

		SpoutPlayer sPlayer = SpoutManager.getPlayer(viewer);

		if (Conf.spoutFactionTagsOverNames || Conf.spoutFactionTitlesOverNames) {
			if (viewedFaction.isNormal()) {
				String addTag = "";
				if (Conf.spoutFactionTagsOverNames) {
					addTag += viewedFaction.getTag(relation.getColor().toString() + "[") + "]";
				}
				String rolePrefix = viewedRole.getPrefix();
				if (Conf.spoutFactionTitlesOverNames && (!viewedTitle.isEmpty() || !rolePrefix.isEmpty())) {
					addTag += (addTag.isEmpty() ? "" : " ") + viewedRole.getPrefix() + viewedTitle;
				}
				spoutApp.setPlayerTitle(sPlayer, viewed, addTag + "\n" + viewed.getDisplayName());
			}
			else {
				spoutApp.setPlayerTitle(sPlayer, viewed, viewed.getDisplayName());
			}
		}

		if (
			   (Conf.spoutFactionAdminCapes && viewedRole.equals(Role.ADMIN))
			|| (Conf.spoutFactionModeratorCapes && viewedRole.equals(Role.MODERATOR))
			) {
			String cape = "";
			if (!viewedFaction.isNormal()) {
				// yeah, no cape if no faction
			}
			else if (viewedFaction.isPeaceful()) {
				cape = Conf.capePeaceful;
			}
			else if (relation.isNeutral()) {
				cape = Conf.capeNeutral;
			}
			else if (relation.isMember()) {
				cape = Conf.capeMember;
			}
			else if (relation.isEnemy()) {
				cape = Conf.capeEnemy;
			}
			else if (relation.isAlly()) {
				cape = Conf.capeAlly;
			}

			if (cape.isEmpty()) {
				spoutApp.resetPlayerCloak(sPlayer, viewed);
			} else {
				spoutApp.setPlayerCloak(sPlayer, viewed, cape);
			}
		}
		else if (Conf.spoutFactionAdminCapes || Conf.spoutFactionModeratorCapes) {
			spoutApp.resetPlayerCloak(sPlayer, viewed);
		}
	}

}
