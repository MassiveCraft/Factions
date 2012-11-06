package com.massivecraft.factions.integration;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;
import com.massivecraft.factions.struct.TerritoryAccess;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.gui.Color;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.SpoutManager;


public class SpoutMainListener implements Listener
{
	@EventHandler(priority = EventPriority.NORMAL)
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event)
	{
		final FPlayer me = FPlayers.i.get(event.getPlayer());

		SpoutFeatures.updateTitle(me, null);
		SpoutFeatures.updateTitle(null, me);
		updateTerritoryDisplay(me, true);
	}

	//-----------------------------------------------------------------------------------------//
	// Everything below this is handled in here to prevent errors on servers not running Spout
	//-----------------------------------------------------------------------------------------//

	private transient static Map<String, GenericLabel> territoryLabels = new HashMap<String, GenericLabel>();
	private transient static Map<String, NoticeLabel> territoryChangeLabels = new HashMap<String, NoticeLabel>();
	private transient static Map<String, GenericLabel> accessLabels = new HashMap<String, GenericLabel>();
	private final static int SCREEN_WIDTH = 427;
//	private final static int SCREEN_HEIGHT = 240;


	public boolean updateTerritoryDisplay(FPlayer player, boolean notify)
	{
		Player p = player.getPlayer();
		if (p == null)
			return false;

		SpoutPlayer sPlayer = SpoutManager.getPlayer(p);
		if (!sPlayer.isSpoutCraftEnabled() || (Conf.spoutTerritoryDisplaySize <= 0 && ! Conf.spoutTerritoryNoticeShow))
			return false;

		doLabels(player, sPlayer, notify);

		return true;
	}

	public boolean updateAccessInfo(FPlayer player)
	{
		Player p = player.getPlayer();
		if (p == null)
			return false;

		SpoutPlayer sPlayer = SpoutManager.getPlayer(p);
		if (!sPlayer.isSpoutCraftEnabled() || (Conf.spoutTerritoryDisplaySize <= 0 && ! Conf.spoutTerritoryNoticeShow))
			return false;

		FLocation here = player.getLastStoodAt();

		doAccessInfo(player, sPlayer, here);

		return true;
	}

	public void removeTerritoryLabels(String playerName)
	{
		territoryLabels.remove(playerName);
		territoryChangeLabels.remove(playerName);
		accessLabels.remove(playerName);
	}


	private void doLabels(FPlayer player, SpoutPlayer sPlayer, boolean notify)
	{
		FLocation here = player.getLastStoodAt();
		Faction factionHere = Board.getFactionAt(here);
		String tag = factionHere.getColorTo(player).toString() + factionHere.getTag();

		// ----------------------
		// Main territory display
		// ----------------------
		if (Conf.spoutTerritoryDisplayPosition > 0 && Conf.spoutTerritoryDisplaySize > 0)
		{
			GenericLabel label; 
			if (territoryLabels.containsKey(player.getName()))
				label = territoryLabels.get(player.getName());
			else
			{
				label = new GenericLabel();
				label.setWidth(1).setHeight(1);  // prevent Spout's questionable new "no default size" warning
				label.setScale(Conf.spoutTerritoryDisplaySize);

				sPlayer.getMainScreen().attachWidget(P.p, label);
				territoryLabels.put(player.getName(), label);
			}

			String msg = tag;

			if (Conf.spoutTerritoryDisplayShowDescription && !factionHere.getDescription().isEmpty())
				msg += " - " + factionHere.getDescription();

			label.setText(msg);
			alignLabel(label, msg);
			label.setDirty(true);
		}

		// -----------------------
		// Fading territory notice
		// -----------------------
		if (notify && Conf.spoutTerritoryNoticeShow && Conf.spoutTerritoryNoticeSize > 0)
		{
			NoticeLabel label; 
			if (territoryChangeLabels.containsKey(player.getName()))
				label = territoryChangeLabels.get(player.getName());
			else
			{
				label = new NoticeLabel(Conf.spoutTerritoryNoticeLeaveAfterSeconds);
				label.setWidth(1).setHeight(1);  // prevent Spout's questionable new "no default size" warning
				label.setScale(Conf.spoutTerritoryNoticeSize);
				label.setY(Conf.spoutTerritoryNoticeTop);
				sPlayer.getMainScreen().attachWidget(P.p, label);
				territoryChangeLabels.put(player.getName(), label);
			}

			String msg = tag;

			if (Conf.spoutTerritoryNoticeShowDescription && !factionHere.getDescription().isEmpty())
				msg += " - " + factionHere.getDescription();

			label.setText(msg);
			alignLabel(label, msg, 2);
			label.resetNotice();
			label.setDirty(true);
		}

		// and access info, of course
		doAccessInfo(player, sPlayer, here);
	}

	private static final Color accessGrantedColor = new Color(0.2f, 1.0f, 0.2f);
	private static final Color accessDeniedColor = new Color(1.0f, 0.2f, 0.2f);
	private void doAccessInfo(FPlayer player, SpoutPlayer sPlayer, FLocation here)
	{
		if (Conf.spoutTerritoryDisplayPosition <= 0 || Conf.spoutTerritoryDisplaySize <= 0 || ! Conf.spoutTerritoryAccessShow) return;

		// -----------
		// Access Info
		// -----------
		GenericLabel label; 
		if (accessLabels.containsKey(player.getName()))
			label = accessLabels.get(player.getName());
		else
		{
			label = new GenericLabel();
			label.setWidth(1).setHeight(1);  // prevent Spout's questionable new "no default size" warning
			label.setScale(Conf.spoutTerritoryDisplaySize);
			label.setY((int)(10 * Conf.spoutTerritoryDisplaySize));
			sPlayer.getMainScreen().attachWidget(P.p, label);
			accessLabels.put(player.getName(), label);
		}

		String msg = "";
		TerritoryAccess access = Board.getTerritoryAccessAt(here);

		if ( ! access.isDefault())
		{
			if (access.subjectHasAccess(player))
			{
				msg = "access granted";
				label.setTextColor(accessGrantedColor);
			}
			else if (access.subjectAccessIsRestricted(player))
			{
				msg = "access restricted";
				label.setTextColor(accessDeniedColor);
			}
		}

		label.setText(msg);
		alignLabel(label, msg);
		label.setDirty(true);
	}

	// this is only necessary because Spout text size scaling is currently bugged and breaks their built-in alignment methods
	public void alignLabel(GenericLabel label, String text)
	{
		alignLabel(label, text, Conf.spoutTerritoryDisplayPosition);
	}
	public void alignLabel(GenericLabel label, String text, int alignment)
	{
		int labelWidth = (int)((float)GenericLabel.getStringWidth(text) * Conf.spoutTerritoryDisplaySize);
		if (labelWidth > SCREEN_WIDTH)
		{
				label.setX(0);
				return;
		}

		switch (alignment)
		{
			case 1:		// left aligned
				label.setX(0);
				break;
			case 2:		// center aligned
				label.setX((SCREEN_WIDTH - labelWidth) / 2);
				break;
			default:	// right aligned
				label.setX(SCREEN_WIDTH - labelWidth);
		}
	}


	private static class NoticeLabel extends GenericLabel
	{
		private int initial;
		private int countdown;  // current delay countdown

		public NoticeLabel(float secondsOfLife)
		{
			initial = (int)(secondsOfLife * 20);
			resetNotice();
		}

		public final void resetNotice()
		{
			countdown = initial;
		}

		@Override
		public void onTick()
		{
			if (countdown <= 0)
				return;

			this.countdown -= 1;

			if (this.countdown <= 0)
			{
				this.setText("");
				this.setDirty(true);
			}
		}
	}
}