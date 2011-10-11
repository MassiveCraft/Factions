package com.massivecraft.factions.integration;

import java.util.HashMap;
import java.util.Map;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.P;

import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;
import org.getspout.spoutapi.event.spout.SpoutListener;
import org.getspout.spoutapi.gui.GenericLabel;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.getspout.spoutapi.SpoutManager;
//import org.getspout.spoutapi.gui.WidgetAnchor;


public class SpoutMainListener extends SpoutListener
{
	@Override
	public void onSpoutCraftEnable(SpoutCraftEnableEvent event)
	{
		final FPlayer me = FPlayers.i.get(event.getPlayer());

		SpoutFeatures.updateAppearances(me.getPlayer());
		updateTerritoryDisplay(me);
	}


	//-----------------------------------------------------------------------------------------//
	// Everything below this is handled in here to prevent errors on servers not running Spout
	//-----------------------------------------------------------------------------------------//

	private transient static Map<String, GenericLabel> territoryLabels = new HashMap<String, GenericLabel>();
	private final static int SCREEN_WIDTH = 427;
//	private final static int SCREEN_HEIGHT = 240;


	public boolean updateTerritoryDisplay(FPlayer player)
	{
		SpoutPlayer sPlayer = SpoutManager.getPlayer(player.getPlayer());
		if (!sPlayer.isSpoutCraftEnabled() || Conf.spoutTerritoryDisplaySize <= 0)
		{
			return false;
		}

		GenericLabel label; 
		if (territoryLabels.containsKey(player.getName()))
		{
			label = territoryLabels.get(player.getName());
		} else {
			label = new GenericLabel();
			label.setScale(Conf.spoutTerritoryDisplaySize);
/*			// this should work once the Spout team fix it to account for text scaling; we can then get rid of alignLabel method added below
			switch (Conf.spoutTerritoryDisplayPosition) {
				case 1: label.setAlign(WidgetAnchor.TOP_LEFT).setAnchor(WidgetAnchor.TOP_LEFT); break;
				case 2: label.setAlign(WidgetAnchor.TOP_CENTER).setAnchor(WidgetAnchor.TOP_CENTER); break;
				default: label.setAlign(WidgetAnchor.TOP_RIGHT).setAnchor(WidgetAnchor.TOP_RIGHT);
			}
 */
			sPlayer.getMainScreen().attachWidget(P.p, label);
			territoryLabels.put(player.getName(), label);
		}

		Faction factionHere = Board.getFactionAt(new FLocation(player));
		String msg = factionHere.getTag();

		if (Conf.spoutTerritoryDisplayShowDescription && factionHere.getDescription().length() > 0)
		{
			msg += " - "+factionHere.getDescription();
		}

		label.setTextColor(SpoutFeatures.getSpoutColor(player.getRelationColor(factionHere), 0));
		label.setText(msg);
		alignLabel(label, msg);
		label.setDirty(true);
		
		return true;
	}

	// this is only necessary because Spout text size scaling is currently bugged and breaks their built-in alignment methods
	public void alignLabel(GenericLabel label, String text)
	{
		int labelWidth = (int)((float)GenericLabel.getStringWidth(text) * Conf.spoutTerritoryDisplaySize);
		if (labelWidth > SCREEN_WIDTH)
		{
				label.setX(0);
				return;
		}

		switch (Conf.spoutTerritoryDisplayPosition)
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

	public void removeTerritoryLabel(String playerName)
	{
		territoryLabels.remove(playerName);
	}
}