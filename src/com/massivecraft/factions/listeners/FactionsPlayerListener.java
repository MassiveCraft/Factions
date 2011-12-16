package com.massivecraft.factions.listeners;

import java.util.logging.Logger;
import java.util.Iterator;
import java.util.UnknownFormatConversionException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.massivecraft.factions.Board;
import com.massivecraft.factions.Conf;
import com.massivecraft.factions.FLocation;
import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.Permission;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.zcore.util.TextUtil;

import java.util.logging.Level;



public class FactionsPlayerListener extends PlayerListener
{
	public P p;
	public FactionsPlayerListener(P p)
	{
		this.p = p;
	}
	
	@Override
	public void onPlayerChat(PlayerChatEvent event)
	{
		if (event.isCancelled()) return;
		
		Player talkingPlayer = event.getPlayer();
		String msg = event.getMessage();
		
		// ... it was not a command. This means that it is a chat message!
		FPlayer me = FPlayers.i.get(talkingPlayer);
		
		// Are we to insert the Faction tag into the format?
		// If we are not to insert it - we are done.
		if ( ! Conf.chatTagEnabled || Conf.chatTagHandledByAnotherPlugin)
		{
			return;
		}

		int InsertIndex = 0;
		String eventFormat = event.getFormat();
		
		if (!Conf.chatTagReplaceString.isEmpty() && eventFormat.contains(Conf.chatTagReplaceString))
		{
			// we're using the "replace" method of inserting the faction tags
			// if they stuck "{FACTION_TITLE}" in there, go ahead and do it too
			if (eventFormat.contains("{FACTION_TITLE}"))
			{
				eventFormat = eventFormat.replace("{FACTION_TITLE}", me.getTitle());
			}
			InsertIndex = eventFormat.indexOf(Conf.chatTagReplaceString);
			eventFormat = eventFormat.replace(Conf.chatTagReplaceString, "");
			Conf.chatTagPadAfter = false;
			Conf.chatTagPadBefore = false;
		}
		else if (!Conf.chatTagInsertAfterString.isEmpty() && eventFormat.contains(Conf.chatTagInsertAfterString))
		{
			// we're using the "insert after string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertAfterString) + Conf.chatTagInsertAfterString.length();
		}
		else if (!Conf.chatTagInsertBeforeString.isEmpty() && eventFormat.contains(Conf.chatTagInsertBeforeString))
		{
			// we're using the "insert before string" method
			InsertIndex = eventFormat.indexOf(Conf.chatTagInsertBeforeString);
		}
		else
		{
			// we'll fall back to using the index place method
			InsertIndex = Conf.chatTagInsertIndex;
			if (InsertIndex > eventFormat.length())
				return;
		}
		
		String formatStart = eventFormat.substring(0, InsertIndex) + ((Conf.chatTagPadBefore && !me.getChatTag().isEmpty()) ? " " : "");
		String formatEnd = ((Conf.chatTagPadAfter && !me.getChatTag().isEmpty()) ? " " : "") + eventFormat.substring(InsertIndex);
		
		String nonColoredMsgFormat = formatStart + me.getChatTag().trim() + formatEnd;
		
		// Relation Colored?
		if (Conf.chatTagRelationColored)
		{
			// We must choke the standard message and send out individual messages to all players
			// Why? Because the relations will differ.
			event.setCancelled(true);
			
			for (Player listeningPlayer : event.getRecipients())
			{
				FPlayer you = FPlayers.i.get(listeningPlayer);
				String yourFormat = formatStart + me.getChatTag(you).trim() + formatEnd;
				try
				{
					listeningPlayer.sendMessage(String.format(yourFormat, talkingPlayer.getDisplayName(), msg));
				}
				catch (UnknownFormatConversionException ex)
				{
					P.p.log(Level.SEVERE, "Critical error in chat message formatting! Complete format string: "+yourFormat);
					P.p.log(Level.SEVERE, "First half of event.getFormat() string: "+formatStart);
					P.p.log(Level.SEVERE, "Second half of event.getFormat() string: "+formatEnd);
					P.p.log(Level.SEVERE, "NOTE: To fix this quickly, running this command should work: f config chatTagInsertIndex 0");
					P.p.log(Level.SEVERE, "For a more proper fix, please read the chat configuration notes on the configuration page of the Factions user guide.");
					ex.printStackTrace();
					return;
				}
			}
			
			// Write to the log... We will write the non colored message.
			String nonColoredMsg = ChatColor.stripColor(String.format(nonColoredMsgFormat, talkingPlayer.getDisplayName(), msg));
			Logger.getLogger("Minecraft").info(nonColoredMsg);
		}
		else
		{
			// No relation color.
			event.setFormat(nonColoredMsgFormat);
		}
	}
	
	@Override
	public void onPlayerJoin(PlayerJoinEvent event)
	{
		// Make sure that all online players do have a fplayer.
		final FPlayer me = FPlayers.i.get(event.getPlayer());
		
		// Update the lastLoginTime for this fplayer
		me.setLastLoginTime(System.currentTimeMillis());
		
		// Run the member auto kick routine. Twice to get to the admins...
		FPlayers.i.autoLeaveOnInactivityRoutine();
		FPlayers.i.autoLeaveOnInactivityRoutine();

		SpoutFeatures.updateAppearancesShortly(event.getPlayer());
	}
	
    @Override
    public void onPlayerQuit(PlayerQuitEvent event)
    {
		// Make sure player's power is up to date when they log off.
		FPlayer me = FPlayers.i.get(event.getPlayer());
		me.getPower();
		Faction myFaction = me.getFaction();
		if (myFaction != null)
		{
			myFaction.memberLoggedOff();
		}
		SpoutFeatures.playerDisconnect(me);
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		// Did we change block?
		if (event.getFrom().equals(event.getTo())
			|| (event.getFrom().getBlockX() == event.getTo().getBlockX() && event.getFrom().getBlockZ() == event.getTo().getBlockZ())
			) return;

		Player player = event.getPlayer();
		FPlayer me = FPlayers.i.get(player);
		
		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(player.getLocation());
		
		if (from.equals(to))
		{
			return;
		}
		
		// Yes we did change coord (:
		
		me.setLastStoodAt(to);

		// Did we change "host"(faction)?
		boolean spoutClient = SpoutFeatures.availableFor(player);
		Faction factionFrom = Board.getFactionAt(from);
		Faction factionTo = Board.getFactionAt(to);
		boolean changedFaction = (factionFrom != factionTo);

		if (changedFaction && SpoutFeatures.updateTerritoryDisplay(me))
			changedFaction = false;

		if (me.isMapAutoUpdating())
		{
			me.sendMessage(Board.getMap(me.getFaction(), to, player.getLocation().getYaw()));

			if (spoutClient && Conf.spoutTerritoryOwnersShow)
				SpoutFeatures.updateOwnerList(me);
		}
		else
		{
			Faction myFaction = me.getFaction();
			String ownersTo = myFaction.getOwnerListString(to);

			if (changedFaction)
			{
				me.sendFactionHereMessage();
				if
				(
					Conf.ownedAreasEnabled
					&&
					Conf.ownedMessageOnBorder
					&&
					(
						!spoutClient
						||
						!Conf.spoutTerritoryOwnersShow
					)
					&&
					myFaction == factionTo
					&&
					!ownersTo.isEmpty()
				)
				{
					me.sendMessage(Conf.ownedLandMessage+ownersTo);
				}
			}
			else if (spoutClient && Conf.spoutTerritoryOwnersShow)
			{
				SpoutFeatures.updateOwnerList(me);
			}
			else if
			(
				Conf.ownedAreasEnabled
				&&
				Conf.ownedMessageInsideTerritory
				&&
				factionFrom == factionTo
				&&
				myFaction == factionTo
			)
			{
				String ownersFrom = myFaction.getOwnerListString(from);
				if (Conf.ownedMessageByChunk || !ownersFrom.equals(ownersTo))
				{
					if (!ownersTo.isEmpty())
						me.sendMessage(Conf.ownedLandMessage+ownersTo);
					else if (!Conf.publicLandMessage.isEmpty())
						me.sendMessage(Conf.publicLandMessage);
				}
			}
		}
		
		if (me.getAutoClaimFor() != null)
		{
			me.attemptClaim(me.getAutoClaimFor(), player.getLocation(), true);
		}
		else if (me.isAutoSafeClaimEnabled())
		{
			if ( ! Permission.MANAGE_SAFE_ZONE.has(player))
			{
				me.setIsAutoSafeClaimEnabled(false);
			}
			else
			{
				FLocation playerFlocation = new FLocation(me);

				if (!Board.getFactionAt(playerFlocation).isSafeZone())
				{
					Board.setFactionAt(Factions.i.getSafeZone(), playerFlocation);
					me.msg("<i>This land is now a safe zone.");
				}
			}
		}
		else if (me.isAutoWarClaimEnabled())
		{
			if ( ! Permission.MANAGE_WAR_ZONE.has(player))
			{
				me.setIsAutoWarClaimEnabled(false);
			}
			else
			{
				FLocation playerFlocation = new FLocation(me);

				if (!Board.getFactionAt(playerFlocation).isWarZone())
				{
					Board.setFactionAt(Factions.i.getWarZone(), playerFlocation);
					me.msg("<i>This land is now a war zone.");
				}
			}
		}
	}

    @Override
    public void onPlayerInteract(PlayerInteractEvent event)
    {
		if (event.isCancelled()) return;

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null)
		{
			return;  // clicked in air, apparently
		}

		if ( ! canPlayerUseBlock(player, block, false))
		{
			event.setCancelled(true);
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK)
		{
			return;  // only interested on right-clicks for below
		}

		if ( ! playerCanUseItemHere(player, block.getLocation(), event.getMaterial(), false))
		{
			event.setCancelled(true);
			return;
		}
	}

	public static boolean playerCanUseItemHere(Player player, Location location, Material material, boolean justCheck)
	{
		FPlayer me = FPlayers.i.get(player);
		if (me.isAdminBypassing())
			return true;

		FLocation loc = new FLocation(location);
		Faction otherFaction = Board.getFactionAt(loc);

		if (otherFaction.hasPlayersOnline())
		{
			if ( ! Conf.territoryDenyUseageMaterials.contains(material))
				return true; // Item isn't one we're preventing for online factions.
		}
		else
		{
			if ( ! Conf.territoryDenyUseageMaterialsWhenOffline.contains(material))
				return true; // Item isn't one we're preventing for offline factions.
		}

		if (otherFaction.isNone())
		{
			if (!Conf.wildernessDenyUseage || Conf.worldsNoWildernessProtection.contains(location.getWorld().getName()))
				return true; // This is not faction territory. Use whatever you like here.
			
			if (!justCheck)
				me.msg("<b>You can't use <h>%s<b> in the wilderness.", TextUtil.getMaterialName(material));

			return false;
		}
		else if (otherFaction.isSafeZone())
		{
			if (!Conf.safeZoneDenyUseage || Permission.MANAGE_SAFE_ZONE.has(player))
				return true;

			if (!justCheck)
				me.msg("<b>You can't use <h>%s<b> in a safe zone.", TextUtil.getMaterialName(material));

			return false;
		}
		else if (otherFaction.isWarZone())
		{
			if (!Conf.warZoneDenyUseage || Permission.MANAGE_WAR_ZONE.has(player))
				return true;

			if (!justCheck)
				me.msg("<b>You can't use <h>%s<b> in a war zone.", TextUtil.getMaterialName(material));

			return false;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// Cancel if we are not in our own territory
		if (rel.confDenyUseage())
		{
			if (!justCheck)
				me.msg("<b>You can't use <h>%s<b> in the territory of <h>%s<b>.", TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaDenyUseage && !otherFaction.playerHasOwnershipRights(me, loc))
		{
			if (!justCheck)
				me.msg("<b>You can't use <h>%s<b> in this territory, it is owned by: %s<b>.", TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));

			return false;
		}

		return true;
	}

	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck)
	{
		FPlayer me = FPlayers.i.get(player);
		if (me.isAdminBypassing())
			return true;

		Material material = block.getType();
		FLocation loc = new FLocation(block);
		Faction otherFaction = Board.getFactionAt(loc);

		// no door/chest/whatever protection in wilderness, war zones, or safe zones
		if (!otherFaction.isNormal())
			return true;

		// We only care about some material types.
		if (otherFaction.hasPlayersOnline())
		{
			if ( ! Conf.territoryProtectedMaterials.contains(material))
				return true;
		}
		else
		{
			if ( ! Conf.territoryProtectedMaterialsWhenOffline.contains(material))
				return true;
		}

		Faction myFaction = me.getFaction();
		Relation rel = myFaction.getRelationTo(otherFaction);

		// You may use any block unless it is another faction's territory...
		if (rel.isNeutral() || (rel.isEnemy() && Conf.territoryEnemyProtectMaterials) || (rel.isAlly() && Conf.territoryAllyProtectMaterials))
		{
			if (!justCheck)
				me.msg("<b>You can't use <h>%s<b> in the territory of <h>%s<b>.", TextUtil.getMaterialName(material), otherFaction.getTag(myFaction));

			return false;
		}

		// Also cancel if player doesn't have ownership rights for this claim
		if (Conf.ownedAreasEnabled && Conf.ownedAreaProtectMaterials && !otherFaction.playerHasOwnershipRights(me, loc))
		{
			if (!justCheck)
				me.msg("<b>You can't use <h>%s<b> in this territory, it is owned by: %s<b>.", TextUtil.getMaterialName(material), otherFaction.getOwnerListString(loc));
			
			return false;
		}

		return true;
	}

	@Override
	public void onPlayerRespawn(PlayerRespawnEvent event)
	{
		FPlayer me = FPlayers.i.get(event.getPlayer());
		Location home = me.getFaction().getHome();
		if
		(
			Conf.homesEnabled
			&&
			Conf.homesTeleportToOnDeath
			&&
			home != null
			&&
			(
				Conf.homesRespawnFromNoPowerLossWorlds
				||
				! Conf.worldsNoPowerLoss.contains(event.getPlayer().getWorld().getName())
			)
		)
		{
			event.setRespawnLocation(home);
		}
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@Override
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		if (event.isCancelled()) return;

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if ( ! playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false))
		{
			event.setCancelled(true);
			return;
		}
	}
	@Override
	public void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		if (event.isCancelled()) return;

		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if ( ! playerCanUseItemHere(player, block.getLocation(), event.getBucket(), false))
		{
			event.setCancelled(true);
			return;
		}
	}

	@Override
	public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event)
	{
		if (event.isCancelled()) return;

		if (preventCommand(event.getMessage().toLowerCase(), event.getPlayer()))
		{
			event.setCancelled(true);
		}
	}

	public static boolean preventCommand(String fullCmd, Player player)
	{
		if ((Conf.territoryNeutralDenyCommands.isEmpty() && Conf.territoryEnemyDenyCommands.isEmpty()))
		{
			return false;
		}

		FPlayer me = FPlayers.i.get(player);

		if (!me.isInOthersTerritory())
		{
			return false;
		}

		Relation rel = me.getRelationToLocation();
		if (rel.isAtLeast(Relation.ALLY))
		{
			return false;
		}

		String shortCmd = fullCmd.substring(1);	// Get rid of the slash at the beginning
		
		if
		(
			rel.isNeutral()
			&&
			! Conf.territoryNeutralDenyCommands.isEmpty()
			&&
			! me.isAdminBypassing()
		)
		{
			Iterator<String> iter = Conf.territoryNeutralDenyCommands.iterator();
			String cmdCheck;
			while (iter.hasNext())
			{
				cmdCheck = iter.next();
				if (cmdCheck == null)
				{
					iter.remove();
					continue;
				}

				cmdCheck = cmdCheck.toLowerCase();
				if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck))
				{
					me.msg("<b>You can't use the command \""+fullCmd+"\" in neutral territory.");
					return true;
				}
			}
		}
		else if
		(
			rel.isEnemy()
			&&
			! Conf.territoryEnemyDenyCommands.isEmpty()
			&&
			! me.isAdminBypassing()
		)
		{
			Iterator<String> iter = Conf.territoryEnemyDenyCommands.iterator();
			String cmdCheck;
			while (iter.hasNext())
			{
				cmdCheck = iter.next();
				if (cmdCheck == null)
				{
					iter.remove();
					continue;
				}

				cmdCheck = cmdCheck.toLowerCase();
				if (fullCmd.startsWith(cmdCheck) || shortCmd.startsWith(cmdCheck))
				{
					me.msg("<b>You can't use the command \""+fullCmd+"\" in enemy territory.");
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public void onPlayerKick(PlayerKickEvent event)
	{
		if (event.isCancelled()) return;

		FPlayer badGuy = FPlayers.i.get(event.getPlayer());
		if (badGuy == null)
		{
			return;
		}

		SpoutFeatures.playerDisconnect(badGuy);

		// if player was banned (not just kicked), get rid of their stored info
		if (event.getReason().equals("Banned by admin."))
		{
			badGuy.leave(false);
			badGuy.markForDeletion(true);
		}
	}
}
