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
import com.massivecraft.factions.P;
import com.massivecraft.factions.integration.SpoutFeatures;
import com.massivecraft.factions.struct.FPerm;
import com.massivecraft.factions.struct.Rel;

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
		SpoutFeatures.playerDisconnect(me);
	}
	
	@Override
	public void onPlayerMove(PlayerMoveEvent event)
	{
		Player player = event.getPlayer();
		FPlayer me = FPlayers.i.get(player);
		
		// Did we change block?
		if (event.getFrom().equals(event.getTo())) return;
		
		// Did we change coord?
		FLocation from = me.getLastStoodAt();
		FLocation to = new FLocation(player.getLocation());
		
		if (from.equals(to)) return;
		
		// Yes we did change coord (:
		
		me.setLastStoodAt(to);
		
		if (me.isMapAutoUpdating())
		{
			me.sendMessage(Board.getMap(me.getFaction(), to, player.getLocation().getYaw()));
		}
		else
		{
			// Did we change "host"(faction)?
			Faction factionFrom = Board.getFactionAt(from);
			Faction factionTo = Board.getFactionAt(to);

			if (factionFrom != factionTo)
			{
				me.sendFactionHereMessage();
			}
		}
		
		if (me.getAutoClaimFor() != null)
		{
			me.attemptClaim(me.getAutoClaimFor(), player.getLocation(), true);
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

    // TODO: Refactor ! justCheck    -> to informIfNot
    // TODO: Possibly incorporate pain build... 
    public static boolean playerCanUseItemHere(Player player, Location loc, Material material, boolean justCheck)
	{
		FPlayer me = FPlayers.i.get(player);
		if (me.hasAdminMode()) return true;
		if (Conf.materialsEditTools.contains(material) && ! FPerm.BUILD.has(me, loc, ! justCheck)) return false;
		return true;
	}
	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck)
	{
		FPlayer me = FPlayers.i.get(player);
		if (me.hasAdminMode()) return true;
		Location loc = block.getLocation();
		Material material = block.getType();
		
		if (Conf.materialsEditOnInteract.contains(material) && ! FPerm.BUILD.has(me, loc, ! justCheck)) return false;
		if (Conf.materialsContainer.contains(material) && ! FPerm.CONTAINER.has(me, loc, ! justCheck)) return false;
		if (Conf.materialsDoor.contains(material)      && ! FPerm.DOOR.has(me, loc, ! justCheck)) return false;
		if (material == Material.STONE_BUTTON          && ! FPerm.BUTTON.has(me, loc, ! justCheck)) return false;
		if (material == Material.LEVER                 && ! FPerm.LEVER.has(me, loc, ! justCheck)) return false;
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

		Rel rel = me.getRelationToLocation();
		if (rel.isAtLeast(Rel.TRUCE))
		{
			return false;
		}

		String shortCmd = fullCmd.substring(1);	// Get rid of the slash at the beginning
		
		if
		(
			rel == Rel.NEUTRAL
			&&
			! Conf.territoryNeutralDenyCommands.isEmpty()
			&&
			! me.hasAdminMode()
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
			rel == Rel.ENEMY
			&&
			! Conf.territoryEnemyDenyCommands.isEmpty()
			&&
			! me.hasAdminMode()
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
		}
	}
}
