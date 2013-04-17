package com.massivecraft.factions.integration;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import com.massivecraft.factions.BoardColl;
import com.massivecraft.factions.ConfServer;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayerColl;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.mcore.ps.PS;

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
		final FPlayer me = FPlayerColl.get().get(event.getPlayer());

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
		if (!sPlayer.isSpoutCraftEnabled() || (ConfServer.spoutTerritoryDisplaySize <= 0 && ! ConfServer.spoutTerritoryNoticeShow))
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
		if (!sPlayer.isSpoutCraftEnabled() || (ConfServer.spoutTerritoryDisplaySize <= 0 && ! ConfServer.spoutTerritoryNoticeShow))
			return false;

		PS here = player.getCurrentChunk();

		this.doAccessInfo(player, sPlayer, here);

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
		PS here = player.getCurrentChunk();
		Faction factionHere = BoardColl.get().getFactionAt(here);
		String tag = factionHere.getColorTo(player).toString() + factionHere.getTag();

		// ----------------------
		// Main territory display
		// ----------------------
		if (ConfServer.spoutTerritoryDisplayPosition > 0 && ConfServer.spoutTerritoryDisplaySize > 0)
		{
			GenericLabel label; 
			if (territoryLabels.containsKey(player.getName()))
				label = territoryLabels.get(player.getName());
			else
			{
				label = new GenericLabel();
				label.setWidth(1).setHeight(1);  // prevent Spout's questionable new "no default size" warning
				label.setScale(ConfServer.spoutTerritoryDisplaySize);

				sPlayer.getMainScreen().attachWidget(Factions.get(), label);
				territoryLabels.put(player.getName(), label);
			}

			String msg = tag;

			if (ConfServer.spoutTerritoryDisplayShowDescription && factionHere.hasDescription())
			{
				msg += " - " + factionHere.getDescription();
			}

			label.setText(msg);
			alignLabel(label, msg);
			label.setDirty(true);
		}

		// -----------------------
		// Fading territory notice
		// -----------------------
		if (notify && ConfServer.spoutTerritoryNoticeShow && ConfServer.spoutTerritoryNoticeSize > 0)
		{
			NoticeLabel label; 
			if (territoryChangeLabels.containsKey(player.getName()))
				label = territoryChangeLabels.get(player.getName());
			else
			{
				label = new NoticeLabel(ConfServer.spoutTerritoryNoticeLeaveAfterSeconds);
				label.setWidth(1).setHeight(1);  // prevent Spout's questionable new "no default size" warning
				label.setScale(ConfServer.spoutTerritoryNoticeSize);
				label.setY(ConfServer.spoutTerritoryNoticeTop);
				sPlayer.getMainScreen().attachWidget(Factions.get(), label);
				territoryChangeLabels.put(player.getName(), label);
			}

			String msg = tag;

			if (ConfServer.spoutTerritoryNoticeShowDescription && factionHere.hasDescription())
			{
				msg += " - " + factionHere.getDescription();
			}

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
	private void doAccessInfo(FPlayer player, SpoutPlayer sPlayer, PS here)
	{
		if (ConfServer.spoutTerritoryDisplayPosition <= 0 || ConfServer.spoutTerritoryDisplaySize <= 0 || ! ConfServer.spoutTerritoryAccessShow) return;

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
			label.setScale(ConfServer.spoutTerritoryDisplaySize);
			label.setY((int)(10 * ConfServer.spoutTerritoryDisplaySize));
			sPlayer.getMainScreen().attachWidget(Factions.get(), label);
			accessLabels.put(player.getName(), label);
		}

		String msg = "";
		TerritoryAccess access = BoardColl.get().getTerritoryAccessAt(here);

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
		alignLabel(label, text, ConfServer.spoutTerritoryDisplayPosition);
	}
	public void alignLabel(GenericLabel label, String text, int alignment)
	{
		int labelWidth = (int)((float)GenericLabel.getStringWidth(text) * ConfServer.spoutTerritoryDisplaySize);
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