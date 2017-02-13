package com.massivecraft.factions.engine;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.Zombie;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityBreakDoorEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.PlayerRoleComparator;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.Board;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.factions.event.EventFactionsFactionShowAsync;
import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.massivecraft.factions.event.EventFactionsPowerChange.PowerChangeReason;
import com.massivecraft.factions.integration.Econ;
import com.massivecraft.factions.integration.spigot.IntegrationSpigot;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.PriorityLines;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.event.EventMassiveCorePlayerLeave;
import com.massivecraft.massivecore.mixin.MixinActual;
import com.massivecraft.massivecore.mixin.MixinMessage;
import com.massivecraft.massivecore.mixin.MixinTitle;
import com.massivecraft.massivecore.mixin.MixinWorld;
import com.massivecraft.massivecore.money.Money;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.PlayerUtil;
import com.massivecraft.massivecore.util.TimeDiffUtil;
import com.massivecraft.massivecore.util.TimeUnit;
import com.massivecraft.massivecore.util.Txt;

public class EngineMain extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static EngineMain i = new EngineMain();
	public static EngineMain get() { return i; }
	
	// -------------------------------------------- //
	// CONSTANTS
	// -------------------------------------------- //
	
	public static final Set<SpawnReason> NATURAL_SPAWN_REASONS = new MassiveSet<SpawnReason>(
		SpawnReason.NATURAL,
		SpawnReason.JOCKEY,
		SpawnReason.CHUNK_GEN,
		SpawnReason.OCELOT_BABY,
		SpawnReason.NETHER_PORTAL,
		SpawnReason.MOUNT
	);
	
	// -------------------------------------------- //
	// FACTION SHOW
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onFactionShow(EventFactionsFactionShowAsync event)
	{
		final int tableCols = 4;
		final CommandSender sender = event.getSender();
		final MPlayer mplayer = event.getMPlayer();
		final Faction faction = event.getFaction();
		final boolean normal = faction.isNormal();
		final Map<String, PriorityLines> idPriorityLiness = event.getIdPriorityLiness();
		String none = Txt.parse("<silver><italic>none");
		
		// ID
		if (mplayer.isOverriding())
		{
			show(idPriorityLiness, Const.SHOW_ID_FACTION_ID, Const.SHOW_PRIORITY_FACTION_ID, "ID", faction.getId());
		}
		
		// DESCRIPTION
		show(idPriorityLiness, Const.SHOW_ID_FACTION_DESCRIPTION, Const.SHOW_PRIORITY_FACTION_DESCRIPTION, "Description", faction.getDescription());
		
		// SECTION: NORMAL
		if (normal)
		{
			// AGE
			long ageMillis = faction.getCreatedAtMillis() - System.currentTimeMillis();
			LinkedHashMap<TimeUnit, Long> ageUnitcounts = TimeDiffUtil.limit(TimeDiffUtil.unitcounts(ageMillis, TimeUnit.getAllButMillis()), 3);
			String ageDesc = TimeDiffUtil.formatedVerboose(ageUnitcounts, "<i>");
			show(idPriorityLiness, Const.SHOW_ID_FACTION_AGE, Const.SHOW_PRIORITY_FACTION_AGE, "Age", ageDesc);
			
			// FLAGS
			// We display all editable and non default ones. The rest we skip.
			List<String> flagDescs = new LinkedList<String>();
			for (Entry<MFlag, Boolean> entry : faction.getFlags().entrySet())
			{
				final MFlag mflag = entry.getKey();
				if (mflag == null) continue;
				
				final Boolean value = entry.getValue();
				if (value == null) continue;
				
				if ( ! mflag.isInteresting(value)) continue;
				
				String flagDesc = Txt.parse(value ? "<g>" : "<b>") + mflag.getName();
				flagDescs.add(flagDesc);
			}
			String flagsDesc = Txt.parse("<silver><italic>default");
			if ( ! flagDescs.isEmpty())
			{
				flagsDesc = Txt.implode(flagDescs, Txt.parse(" <i>| "));
			}
			show(idPriorityLiness, Const.SHOW_ID_FACTION_FLAGS, Const.SHOW_PRIORITY_FACTION_FLAGS, "Flags", flagsDesc);
			
			// POWER
			double powerBoost = faction.getPowerBoost();
			String boost = (powerBoost == 0.0) ? "" : (powerBoost > 0.0 ? " (bonus: " : " (penalty: ") + powerBoost + ")";
			String powerDesc = Txt.parse("%d/%d/%d%s", faction.getLandCount(), faction.getPowerRounded(), faction.getPowerMaxRounded(), boost);
			show(idPriorityLiness, Const.SHOW_ID_FACTION_POWER, Const.SHOW_PRIORITY_FACTION_POWER, "Land / Power / Maxpower", powerDesc);

			// SECTION: ECON
			if (Econ.isEnabled())
			{
				// LANDVALUES
				List<String> landvalueLines = new LinkedList<String>();
				long landCount = faction.getLandCount();
				for (EventFactionsChunkChangeType type : EventFactionsChunkChangeType.values())
				{
					Double money = MConf.get().econChunkCost.get(type);
					if (money == null) continue;
					if (money == 0) continue;
					money *= landCount;
					
					String word = "Cost";
					if (money <= 0)
					{
						word = "Reward";
						money *= -1;
					}
					
					String key = Txt.parse("Total Land %s %s", type.toString().toLowerCase(), word);
					String value = Txt.parse("<h>%s", Money.format(money));
					String line = show(key, value);
					landvalueLines.add(line);
				}
				idPriorityLiness.put(Const.SHOW_ID_FACTION_LANDVALUES, new PriorityLines(Const.SHOW_PRIORITY_FACTION_LANDVALUES, landvalueLines));
				
				// BANK
				if (MConf.get().bankEnabled)
				{
					double bank = Money.get(faction);
					String bankDesc = Txt.parse("<h>%s", Money.format(bank, true));
					show(idPriorityLiness, Const.SHOW_ID_FACTION_BANK, Const.SHOW_PRIORITY_FACTION_BANK, "Bank", bankDesc);
				}
			}
		}

		// FOLLOWERS
		List<String> followerLines = new ArrayList<String>();
		
		List<String> followerNamesOnline = new ArrayList<String>();
		List<String> followerNamesOffline = new ArrayList<String>();
		
		List<MPlayer> followers = faction.getMPlayers();
		Collections.sort(followers, PlayerRoleComparator.get());
		for (MPlayer follower : followers)
		{
			if (follower.isOnline(sender))
			{
				followerNamesOnline.add(follower.getNameAndTitle(mplayer));
			}
			else if (normal)
			{
				// For the non-faction we skip the offline members since they are far to many (infinite almost)
				followerNamesOffline.add(follower.getNameAndTitle(mplayer));
			}
		}
		
		String headerOnline = Txt.parse("<a>Followers Online (%s):", followerNamesOnline.size());
		followerLines.add(headerOnline);
		if (followerNamesOnline.isEmpty())
		{
			followerLines.add(none);
		}
		else
		{
			followerLines.addAll(table(followerNamesOnline, tableCols));
		}
		
		if (normal)
		{
			String headerOffline = Txt.parse("<a>Followers Offline (%s):", followerNamesOffline.size());
			followerLines.add(headerOffline);
			if (followerNamesOffline.isEmpty())
			{
				followerLines.add(none);
			}
			else
			{
				followerLines.addAll(table(followerNamesOffline, tableCols));
			}
		}
		idPriorityLiness.put(Const.SHOW_ID_FACTION_FOLLOWERS, new PriorityLines(Const.SHOW_PRIORITY_FACTION_FOLLOWERS, followerLines));
	}
	
	public static String show(String key, String value)
	{
		return Txt.parse("<a>%s: <i>%s", key, value);
	}
	
	public static PriorityLines show(int priority, String key, String value)
	{
		return new PriorityLines(priority, show(key, value));
	}
	
	public static void show(Map<String, PriorityLines> idPriorityLiness, String id, int priority, String key, String value)
	{
		idPriorityLiness.put(id, show(priority, key, value));
	}
	
	public static List<String> table(List<String> strings, int cols)
	{
		List<String> ret = new ArrayList<String>();
		
		StringBuilder row = new StringBuilder();
		int count = 0;
		
		Iterator<String> iter = strings.iterator();
		while (iter.hasNext())
		{
			String string = iter.next();
			row.append(string);
			count++;
			
			if (iter.hasNext() && count != cols)
			{
				row.append(Txt.parse(" <i>| "));
			}
			else
			{
				ret.add(row.toString());
				row = new StringBuilder();
				count = 0;
			}
		}
		
		return ret;
	}
	
	// -------------------------------------------- //
	// UPDATE LAST ACTIVITY
	// -------------------------------------------- //

	public static void updateLastActivity(CommandSender sender)
	{
		if (sender == null) throw new RuntimeException("sender");
		if (MUtil.isntSender(sender)) return;
		
		MPlayer mplayer = MPlayer.get(sender);
		mplayer.setLastActivityMillis();
	}
	
	public static void updateLastActivitySoon(final CommandSender sender)
	{
		if (sender == null) throw new RuntimeException("sender");
		Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), new Runnable()
		{
			@Override
			public void run()
			{
				updateLastActivity(sender);
			}
		});
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOWEST)
	public void updateLastActivity(PlayerJoinEvent event)
	{
		// During the join event itself we want to be able to reach the old data.
		// That is also the way the underlying fallback Mixin system does it and we do it that way for the sake of symmetry. 
		// For that reason we wait till the next tick with updating the value.
		updateLastActivitySoon(event.getPlayer());
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOWEST)
	public void updateLastActivity(EventMassiveCorePlayerLeave event)
	{
		// Here we do however update immediately.
		// The player data should be fully updated before leaving the server.
		updateLastActivity(event.getPlayer());
	}
	
	// -------------------------------------------- //
	// MOTD
	// -------------------------------------------- //
	
	public static void motd(PlayerJoinEvent event, EventPriority currentPriority)
	{
		// Gather info ...
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		final MPlayer mplayer = MPlayer.get(player);
		final Faction faction = mplayer.getFaction();
		
		// ... if there is a motd ...
		if ( ! faction.hasMotd()) return; 
				
		// ... and this is the priority we are supposed to act on ...
		if (currentPriority != MConf.get().motdPriority) return;
		
		// ... and this is an actual join ...
		if ( ! MixinActual.get().isActualJoin(event)) return;
		
		// ... then prepare the messages ...
		final List<Object> messages = faction.getMotdMessages();
		
		// ... and send to the player.
		if (MConf.get().motdDelayTicks < 0)
		{
			MixinMessage.get().messageOne(player, messages);
		}
		else
		{
			Bukkit.getScheduler().scheduleSyncDelayedTask(Factions.get(), new Runnable()
			{
				@Override
				public void run()
				{
					MixinMessage.get().messageOne(player, messages);
				}
			}, MConf.get().motdDelayTicks);
		}
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOWEST)
	public void motdLowest(PlayerJoinEvent event)
	{
		motd(event, EventPriority.LOWEST);
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.LOW)
	public void motdLow(PlayerJoinEvent event)
	{
		motd(event, EventPriority.LOW);
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.NORMAL)
	public void motdNormal(PlayerJoinEvent event)
	{
		motd(event, EventPriority.NORMAL);
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.HIGH)
	public void motdHigh(PlayerJoinEvent event)
	{
		motd(event, EventPriority.HIGH);
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.HIGHEST)
	public void motdHighest(PlayerJoinEvent event)
	{
		motd(event, EventPriority.HIGHEST);
	}
	
	// Can't be cancelled
	@EventHandler(priority = EventPriority.MONITOR)
	public void motdMonitor(PlayerJoinEvent event)
	{
		motd(event, EventPriority.MONITOR);
	}
	
	// -------------------------------------------- //
	// CHUNK CHANGE: DETECT
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void onChunksChange(EventFactionsChunksChange event)
	{
		// For security reasons we block the chunk change on any error since an error might block security checks from happening.
		try
		{
			onChunksChangeInner(event);
		}
		catch (Throwable throwable)
		{
			event.setCancelled(true);
			throwable.printStackTrace();
		}
	}
	
	public void onChunksChangeInner(EventFactionsChunksChange event)
	{
		// Args
		final MPlayer mplayer = event.getMPlayer();
		final Faction newFaction = event.getNewFaction();
		final Map<Faction, Set<PS>> currentFactionChunks = event.getOldFactionChunks();
		final Set<Faction> currentFactions = currentFactionChunks.keySet();
		final Set<PS> chunks = event.getChunks();
		
		// Override Mode? Sure!
		if (mplayer.isOverriding()) return;
		
		// CALC: Is there at least one normal faction among the current ones?
		boolean currentFactionsContainsAtLeastOneNormal = false;
		for (Faction currentFaction : currentFactions)
		{
			if (currentFaction.isNormal())
			{
				currentFactionsContainsAtLeastOneNormal = true;
				break;
			}
		}
		
		// If the new faction is normal (not wilderness/none), meaning if we are claiming for a faction ...
		if (newFaction.isNormal())
		{
			// ... ensure claiming is enabled for the worlds of all chunks ...
			for (PS chunk : chunks)
			{
				String worldId = chunk.getWorld();
				if ( ! MConf.get().worldsClaimingEnabled.contains(worldId))
				{
					String worldName = MixinWorld.get().getWorldDisplayName(worldId);
					mplayer.msg("<b>Land claiming is disabled in <h>%s<b>.", worldName);
					event.setCancelled(true);
					return;
				}
			}
			
			// ... ensure we have permission to alter the territory of the new faction ...
			if ( ! MPerm.getPermTerritory().has(mplayer, newFaction, true))
			{
				// NOTE: No need to send a message. We send message from the permission check itself.
				event.setCancelled(true);
				return;
			}
			
			// ... ensure the new faction has enough players to claim ...
			if (newFaction.getMPlayers().size() < MConf.get().claimsRequireMinFactionMembers)
			{
				mplayer.msg("<b>Factions must have at least <h>%s<b> members to claim land.", MConf.get().claimsRequireMinFactionMembers);
				event.setCancelled(true);
				return;
			}
			
			int claimedLandCount = newFaction.getLandCount();
			if ( ! newFaction.getFlag(MFlag.getFlagInfpower()))
			{
				// ... ensure the claim would not bypass the global max limit ...
				if (MConf.get().claimedLandsMax != 0 && claimedLandCount + chunks.size() > MConf.get().claimedLandsMax)
				{
					mplayer.msg("<b>Limit reached. You can't claim more land.");
					event.setCancelled(true);
					return;
				}
			
				// ... ensure the claim would not bypass the global max limit ...
				if (MConf.get().claimedWorldsMax >= 0)
				{
					Set<String> oldWorlds = newFaction.getClaimedWorlds();
					Set<String> newWorlds = PS.getDistinctWorlds(chunks);
					
					Set<String> worlds = new MassiveSet<>();
					worlds.addAll(oldWorlds);
					worlds.addAll(newWorlds);
					
					if (!oldWorlds.containsAll(newWorlds) && worlds.size() > MConf.get().claimedWorldsMax)
					{
						List<String> worldNames = new MassiveList<>();
						for (String world : oldWorlds)
						{
							worldNames.add(MixinWorld.get().getWorldDisplayName(world));
						}
						
						String worldsMax = MConf.get().claimedWorldsMax == 1 ? "world" : "worlds";
						String worldsAlready = oldWorlds.size() == 1 ? "world" : "worlds";
						mplayer.msg("<b>A faction may only be present on <h>%d<b> different %s.", MConf.get().claimedWorldsMax, worldsMax);
						mplayer.msg("%s<i> is already present on <h>%d<i> %s:", newFaction.describeTo(mplayer), oldWorlds.size(), worldsAlready);
						mplayer.message(Txt.implodeCommaAndDot(worldNames, ChatColor.YELLOW.toString()));
						mplayer.msg("<i>Please unclaim bases on other worlds to claim here.");
						
						event.setCancelled(true);
						return;
					}
				}

			}
			
			// ... ensure the claim would not bypass the faction power ...
			if (claimedLandCount + chunks.size() > newFaction.getPowerRounded())
			{
				mplayer.msg("<b>You don't have enough power to claim that land.");
				event.setCancelled(true);
				return;
			}
			
			// ... ensure the claim would not violate distance to neighbors ...
			// HOW: Calculate the factions nearby, excluding the chunks themselves, the faction itself and the wilderness faction.
			// HOW: The chunks themselves will be handled in the "if (oldFaction.isNormal())" section below. 
			Set<PS> nearbyChunks = BoardColl.getNearbyChunks(chunks, MConf.get().claimMinimumChunksDistanceToOthers);
			nearbyChunks.removeAll(chunks);
			Set<Faction> nearbyFactions = BoardColl.getDistinctFactions(nearbyChunks);
			nearbyFactions.remove(FactionColl.get().getNone());
			nearbyFactions.remove(newFaction);
			// HOW: Next we check if the new faction has permission to claim nearby the nearby factions.
			MPerm claimnear = MPerm.getPermClaimnear();
			for (Faction nearbyFaction : nearbyFactions)
			{
				if (claimnear.has(newFaction, nearbyFaction)) continue;
				mplayer.message(claimnear.createDeniedMessage(mplayer, nearbyFaction));
				event.setCancelled(true);
				return;
			}
			
			// ... ensure claims are properly connected ...
			if
			(
				// If claims must be connected ...
				MConf.get().claimsMustBeConnected
				// ... and this faction already has claimed something on this map (meaning it's not their first claim) ... 
				&&
				newFaction.getLandCountInWorld(chunks.iterator().next().getWorld()) > 0
				// ... and none of the chunks are connected to an already claimed chunk for the faction ...
				&&
				! BoardColl.get().isAnyConnectedPs(chunks, newFaction)
				// ... and either claims must always be connected or there is at least one normal faction among the old factions ...
				&&
				( ! MConf.get().claimsCanBeUnconnectedIfOwnedByOtherFaction || currentFactionsContainsAtLeastOneNormal)
			)
			{
				if (MConf.get().claimsCanBeUnconnectedIfOwnedByOtherFaction)
				{
					mplayer.msg("<b>You can only claim additional land which is connected to your first claim or controlled by another faction!");
				}
				else
				{
					mplayer.msg("<b>You can only claim additional land which is connected to your first claim!");
				}
				event.setCancelled(true);
				return;
			}
		}
		
		// For each of the old factions ...
		for (Entry<Faction, Set<PS>> entry : currentFactionChunks.entrySet())
		{
			Faction oldFaction = entry.getKey();
			Set<PS> oldChunks = entry.getValue();
			
			// ... that is an actual faction ...
			if (oldFaction.isNone()) continue;
			
			// ... for which the mplayer lacks permission ...
			if (MPerm.getPermTerritory().has(mplayer, oldFaction, false)) continue;
			
			// ... consider all reasons to forbid "overclaiming/warclaiming" ...
			
			// ... claiming from others may be forbidden ...
			if ( ! MConf.get().claimingFromOthersAllowed)
			{
				mplayer.msg("<b>You may not claim land from others.");
				event.setCancelled(true);
				return;
			}
			
			// ... the relation may forbid ...
			if (oldFaction.getRelationTo(newFaction).isAtLeast(Rel.TRUCE))
			{
				mplayer.msg("<b>You can't claim this land due to your relation with the current owner.");
				event.setCancelled(true);
				return;
			}
			
			// ... the old faction might not be inflated enough ...
			if (oldFaction.getPowerRounded() > oldFaction.getLandCount() - oldChunks.size())
			{
				mplayer.msg("%s<i> owns this land and is strong enough to keep it.", oldFaction.getName(mplayer));
				event.setCancelled(true);
				return;
			}
			
			// ... and you might be trying to claim without starting at the border ...
			if ( ! BoardColl.get().isAnyBorderPs(chunks))
			{
				mplayer.msg("<b>You must start claiming land at the border of the territory.");
				event.setCancelled(true);
				return;
			}
			
			// ... otherwise you may claim from this old faction even though you lack explicit permission from them.
		}
	}
	
	// -------------------------------------------- //
	// CHUNK CHANGE: DETECT
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void chunkChangeDetect(PlayerMoveEvent event)
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
		this.chunkChangeTerritoryInfo(mplayer, player, chunkFrom, chunkTo, factionFrom, factionTo);
		this.chunkChangeAutoClaim(mplayer, chunkTo);
	}
	
	// -------------------------------------------- //
	// CHUNK CHANGE: TERRITORY INFO
	// -------------------------------------------- //
	
	public void chunkChangeTerritoryInfo(MPlayer mplayer, Player player, PS chunkFrom, PS chunkTo, Faction factionFrom, Faction factionTo)
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
	// CHUNK CHANGE: AUTO CLAIM
	// -------------------------------------------- //
	
	public void chunkChangeAutoClaim(MPlayer mplayer, PS chunkTo)
	{
		// If the player is auto claiming ...
		Faction autoClaimFaction = mplayer.getAutoClaimFaction();
		if (autoClaimFaction == null) return;
		
		// ... try claim.
		mplayer.tryClaim(autoClaimFaction, Collections.singletonList(chunkTo));
	}
	
	// -------------------------------------------- //
	// POWER LOSS ON DEATH
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void powerLossOnDeath(PlayerDeathEvent event)
	{
		// If a player dies ...
		Player player = event.getEntity();
		if (MUtil.isntPlayer(player)) return;
		
		// ... and this is the first death event this tick ...
		// (yeah other plugins can case death event to fire twice the same tick)
		if (PlayerUtil.isDuplicateDeathEvent(event)) return;
		
		MPlayer mplayer = MPlayer.get(player);
		
		// ... and powerloss can happen here ...
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player.getLocation()));
		
		if (!faction.getFlag(MFlag.getFlagPowerloss()))
		{
			mplayer.msg("<i>You didn't lose any power since the territory you died in works that way.");
			return;
		}
		
		if (!MConf.get().worldsPowerLossEnabled.contains(player.getWorld()))
		{
			mplayer.msg("<i>You didn't lose any power due to the world you died in.");
			return;
		}
		
		// ... alter the power ...
		double newPower = mplayer.getPower() + mplayer.getPowerPerDeath();
		
		EventFactionsPowerChange powerChangeEvent = new EventFactionsPowerChange(null, mplayer, PowerChangeReason.DEATH, newPower);
		powerChangeEvent.run();
		if (powerChangeEvent.isCancelled()) return;
		newPower = powerChangeEvent.getNewPower();
		
		mplayer.setPower(newPower);
		
		// ... and inform the player.
		// TODO: A progress bar here would be epic :)
		mplayer.msg("<i>Your power is now <h>%.2f / %.2f", newPower, mplayer.getPowerMax());
	}
	
	// -------------------------------------------- //
	// REMOVE PLAYER DATA WHEN BANNED
	// -------------------------------------------- //

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerKick(PlayerKickEvent event)
	{
		// If a player was kicked from the server ...
		Player player = event.getPlayer();

		// ... and if the if player was banned (not just kicked) ...
		//if (!event.getReason().equals("Banned by admin.")) return;
		if (!player.isBanned()) return;
		
		// ... and we remove player data when banned ...
		if (!MConf.get().removePlayerWhenBanned) return;
		
		// ... get rid of their stored info.
		MPlayer mplayer = MPlayerColl.get().get(player, false);
		if (mplayer == null) return;
		
		if (mplayer.getRole() == Rel.LEADER)
		{
			mplayer.getFaction().promoteNewLeader();
		}
		
		mplayer.leave();
		mplayer.detach();
	}
	
	// -------------------------------------------- //
	// VISUALIZE UTIL
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerMoveClearVisualizations(PlayerMoveEvent event)
	{
		if (MUtil.isSameBlock(event)) return;
		
		VisualizeUtil.clear(event.getPlayer());
	}
	
	// -------------------------------------------- //
	// DENY COMMANDS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.LOW, ignoreCancelled = true)
	public void denyCommands(PlayerCommandPreprocessEvent event)
	{
		// If a player is trying to run a command ...
		Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		MPlayer mplayer = MPlayer.get(player);
		
		// ... and the player is not overriding ...
		if (mplayer.isOverriding()) return;
		
		// ... clean up the command ...
		String command = event.getMessage();
		command = Txt.removeLeadingCommandDust(command);
		command = command.toLowerCase();
		command = command.trim();
		
		// ... the command may be denied for members of permanent factions ...
		if (mplayer.hasFaction() && mplayer.getFaction().getFlag(MFlag.getFlagPermanent()) && MUtil.containsCommand(command, MConf.get().denyCommandsPermanentFactionMember))
		{
			mplayer.msg("<b>You can't use \"<h>/%s<b>\" as member of a permanent faction.", command);
			event.setCancelled(true);
			return;
		}
		
		// ... if there is a faction at the players location we fetch the relation now ...
		PS ps = PS.valueOf(player.getLocation()).getChunk(true);
		Faction factionAtPs = BoardColl.get().getFactionAt(ps);
		Rel factionAtRel = null;
		
		if (factionAtPs != null && ! factionAtPs.isNone())
		{
			factionAtRel = factionAtPs.getRelationTo(mplayer);
		}
		
		// ... there maybe be a player in the distance that denies the command ...
		if (MConf.get().denyCommandsDistance > -1 && ! MConf.get().denyCommandsDistanceBypassIn.contains(factionAtRel))
		{	
			for (Player otherplayer : player.getWorld().getPlayers())
			{
				MPlayer othermplayer = MPlayer.get(otherplayer);
				if (othermplayer == mplayer) continue;
				
				double distance = player.getLocation().distance(otherplayer.getLocation());
				if (MConf.get().denyCommandsDistance > distance) continue;
				
				Rel playerRel = mplayer.getRelationTo(othermplayer);
				if ( ! MConf.get().denyCommandsDistanceRelation.containsKey(playerRel)) continue;
				
				String desc = playerRel.getDescPlayerOne();
				
				mplayer.msg("<b>You can't use \"<h>/%s<b>\" as there is <h>%s<b> nearby.", command, desc);
				event.setCancelled(true);
				return;
			}
		}
		
		// ... if there is no relation here then there are no further checks ...
		if (factionAtRel == null) return;
		
		List<String> deniedCommands = MConf.get().denyCommandsTerritoryRelation.get(factionAtRel);
		if (deniedCommands == null) return;
		if ( ! MUtil.containsCommand(command, deniedCommands)) return;
		
		mplayer.msg("<b>You can't use \"<h>/%s<b>\" in %s territory.", command, Txt.getNicedEnum(factionAtRel));
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// FLAG: MONSTERS & ANIMALS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockMonstersAndAnimals(CreatureSpawnEvent event)
	{
		// If this is a natural spawn ..
		if ( ! NATURAL_SPAWN_REASONS.contains(event.getSpawnReason())) return;
		
		// ... get the spawn location ...
		Location location = event.getLocation();
		if (location == null) return;		
		PS ps = PS.valueOf(location);
		
		// ... get the faction there ...
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction == null) return;
		
		// ... get the entity type ...
		EntityType type = event.getEntityType();
		
		// ... and if this type can't spawn in the faction ...
		if (canSpawn(faction, type)) return;
		
		// ... then cancel.
		event.setCancelled(true);
	}
	
	public static boolean canSpawn(Faction faction, EntityType type)
	{
		if (MConf.get().entityTypesMonsters.contains(type))
		{
			// Monster
			return faction.getFlag(MFlag.getFlagMonsters());
		}
		else if (MConf.get().entityTypesAnimals.contains(type))
		{
			// Animal
			return faction.getFlag(MFlag.getFlagAnimals());
		}
		else
		{
			// Other
			return true;
		}
	}
	
	// -------------------------------------------- //
	// FLAG: EXPLOSIONS
	// -------------------------------------------- //
	
	protected Set<DamageCause> DAMAGE_CAUSE_EXPLOSIONS = EnumSet.of(DamageCause.BLOCK_EXPLOSION, DamageCause.ENTITY_EXPLOSION);
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(HangingBreakEvent event)
	{
		// If a hanging entity was broken by an explosion ...
		if (event.getCause() != RemoveCause.EXPLOSION) return;
		Entity entity = event.getEntity();
	
		// ... and the faction there has explosions disabled ...
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(entity.getLocation()));
		if (faction.isExplosionsAllowed()) return;
		
		// ... then cancel.
		event.setCancelled(true);
	}
	 
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityDamageEvent event)
	{
		// If an explosion damages ...
		if ( ! DAMAGE_CAUSE_EXPLOSIONS.contains(event.getCause())) return;
		
		// ... an entity that is modified on damage ...
		if ( ! MConf.get().entityTypesEditOnDamage.contains(event.getEntityType())) return;
		
		// ... and the faction has explosions disabled ...
		if (BoardColl.get().getFactionAt(PS.valueOf(event.getEntity())).isExplosionsAllowed()) return;
		
		// ... then cancel!
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityExplodeEvent event)
	{
		// Prepare some variables:
		// Current faction
		Faction faction = null;
		// Current allowed
		Boolean allowed = true;
		// Caching to speed things up.
		Map<Faction, Boolean> faction2allowed = new HashMap<Faction, Boolean>();
				
		// If an explosion occurs at a location ...
		Location location = event.getLocation();
		
		// Check the entity. Are explosions disabled there? 
		faction = BoardColl.get().getFactionAt(PS.valueOf(location));
		allowed = faction.isExplosionsAllowed();
		if (allowed == false)
		{
			event.setCancelled(true);
			return;
		}
		faction2allowed.put(faction, allowed);
		
		// Individually check the flag state for each block
		Iterator<Block> iter = event.blockList().iterator();
		while (iter.hasNext())
		{
			Block block = iter.next();
			faction = BoardColl.get().getFactionAt(PS.valueOf(block));
			allowed = faction2allowed.get(faction);
			if (allowed == null)
			{
				allowed = faction.isExplosionsAllowed();
				faction2allowed.put(faction, allowed);
			}
			
			if (allowed == false) iter.remove();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityChangeBlockEvent event)
	{
		// If a wither is changing a block ...
		Entity entity = event.getEntity();
		if (!(entity instanceof Wither)) return;

		// ... and the faction there has explosions disabled ...
		PS ps = PS.valueOf(event.getBlock());
		Faction faction = BoardColl.get().getFactionAt(ps);
		
		if (faction.isExplosionsAllowed()) return;
		
		// ... stop the block alteration.
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// FLAG: ENDERGRIEF
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockEndergrief(EntityChangeBlockEvent event)
	{
		// If an enderman is changing a block ...
		Entity entity = event.getEntity();
		if (!(entity instanceof Enderman)) return;
		
		// ... and the faction there has endergrief disabled ...
		PS ps = PS.valueOf(event.getBlock());
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction.getFlag(MFlag.getFlagEndergrief())) return;
		
		// ... stop the block alteration.
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// FLAG: ZOMBIEGRIEF
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void denyZombieGrief(EntityBreakDoorEvent event)
	{
		// If a zombie is breaking a door ...
		Entity entity = event.getEntity();
		if (!(entity instanceof Zombie)) return;
		
		// ... and the faction there has zombiegrief disabled ...
		PS ps = PS.valueOf(event.getBlock());
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction.getFlag(MFlag.getFlagZombiegrief())) return;
		
		// ... stop the door breakage.
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// FLAG: FIRE SPREAD
	// -------------------------------------------- //
	
	public void blockFireSpread(Block block, Cancellable cancellable)
	{
		// If the faction at the block has firespread disabled ...
		PS ps = PS.valueOf(block);
		Faction faction = BoardColl.get().getFactionAt(ps);
			
		if (faction.getFlag(MFlag.getFlagFirespread())) return;
		
		// then cancel the event.
		cancellable.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockIgniteEvent event)
	{
		// If fire is spreading ...
		if (event.getCause() != IgniteCause.SPREAD && event.getCause() != IgniteCause.LAVA) return;
		
		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}
	
	// TODO: Is use of this event deprecated?
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockSpreadEvent event)
	{
		// If fire is spreading ...
		if (event.getNewState().getType() != Material.FIRE) return;
		
		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockFireSpread(BlockBurnEvent event)
	{
		// If a block is burning ...
		
		// ... consider blocking it.
		blockFireSpread(event.getBlock(), event);
	}
	
	// -------------------------------------------- //
	// FLAG: BUILD
	// -------------------------------------------- //
	
	public static boolean canPlayerBuildAt(Object senderObject, PS ps, boolean verboose)
	{
		MPlayer mplayer = MPlayer.get(senderObject);
		if (mplayer == null) return false;
		
		String name = mplayer.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		if (mplayer.isOverriding()) return true;
		if (!MPerm.getPermBuild().has(mplayer, ps, false) && MPerm.getPermPainbuild().has(mplayer, ps, false))
		{
			if (verboose)
			{
				Faction hostFaction = BoardColl.get().getFactionAt(ps);
				mplayer.msg("<b>It is painful to build in the territory of %s<b>.", hostFaction.describeTo(mplayer));
				Player player = mplayer.getPlayer();
				if (player != null)
				{
					player.damage(MConf.get().actionDeniedPainAmount);
				}
			}
			return true;
		}
		
		return MPerm.getPermBuild().has(mplayer, ps, verboose);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void blockBuild(BlockPlaceEvent event)
	{
		if (!event.canBuild()) return;

		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), verboose)) return;
		
		event.setBuild(false);
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockBreakEvent event)
	{
		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), verboose)) return;
		
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockDamageEvent event)
	{
		if ( ! event.getInstaBreak()) return;

		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), verboose)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(SignChangeEvent event)
	{
		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), verboose)) return;
		
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonExtendEvent event)
	{
		// Is using Spigot or is checking deactivated by MConf?
		if (IntegrationSpigot.get().isIntegrationActive() || ! MConf.get().handlePistonProtectionThroughDenyBuild) return;
		
		Block block = event.getBlock();
		
		// Targets end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);

		// Factions involved
		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(block));
		Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetBlock));

		// Members of a faction might not have build rights in their own territory, but pistons should still work regardless
		if (targetFaction == pistonFaction) return;

		// if potentially pushing into air/water/lava in another territory, we need to check it out
		if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && ! MPerm.getPermBuild().has(pistonFaction, targetFaction))
		{
			event.setCancelled(true);
		}

		/*
		 * note that I originally was testing the territory of each affected block, but since I found that pistons can only push
		 * up to 12 blocks and the width of any territory is 16 blocks, it should be safe (and much more lightweight) to test
		 * only the final target block as done above
		 */

	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonRetractEvent event)
	{	
		// Is using Spigot or is checking deactivated by MConf?
		if (IntegrationSpigot.get().isIntegrationActive() || ! MConf.get().handlePistonProtectionThroughDenyBuild) return;
				
		// If not a sticky piston, retraction should be fine
		if ( ! event.isSticky()) return;
		
		Block retractBlock = event.getRetractLocation().getBlock();
		PS retractPs = PS.valueOf(retractBlock);

		// if potentially retracted block is just air/water/lava, no worries
		if (retractBlock.isEmpty() || retractBlock.isLiquid()) return;

		// Factions involved
		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(event.getBlock()));
		Faction targetFaction = BoardColl.get().getFactionAt(retractPs);

		// Members of a faction might not have build rights in their own territory, but pistons should still work regardless
		if (targetFaction == pistonFaction) return;

		if (MPerm.getPermBuild().has(pistonFaction, targetFaction)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(HangingPlaceEvent event)
	{
		boolean verboose = ! isFake(event);

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getEntity().getLocation()), verboose)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(HangingBreakEvent event)
	{
		if (! (event instanceof HangingBreakByEntityEvent)) return;
		HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent)event;
		
		Entity breaker = entityEvent.getRemover();
		if (MUtil.isntPlayer(breaker)) return;

		boolean verboose = ! isFake(event);

		if ( ! canPlayerBuildAt(breaker, PS.valueOf(event.getEntity().getLocation()), verboose))
		{
			event.setCancelled(true);
		}
	}
	
	// Check for punching out fires where players should not be able to
	@SuppressWarnings("deprecation")
	@EventHandler(priority = EventPriority.NORMAL)
	public void blockBuild(PlayerInteractEvent event)
	{
		// only need to check right-clicks and physical as of MC 1.4+; good performance boost
		if (event.getAction() != Action.LEFT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) return;
		
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null) return;  // clicked in air, apparently

		if ( ! canPlayerUseBlock(player, block, true))
		{
			event.setCancelled(true);
			return;
		}

		if (event.getAction() != Action.LEFT_CLICK_BLOCK) return;  // only interested on right-clicks for below

		if ( ! playerTrustedCantBreak(player, PS.valueOf(block), event.getMaterial(), true))
		{
			event.setCancelled(true);
			return;
		}
		
		if ( ! playerCanUseItemHere(player, PS.valueOf(block), event.getMaterial(), true))
		{
			event.setCancelled(true);
			return;
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockLiquidFlow(BlockFromToEvent event)
	{
		if ( ! MConf.get().protectionLiquidFlowEnabled) return;
		
		// Prepare fields
		Block fromBlock = event.getBlock();
		int fromCX = fromBlock.getX() >> 4;
		int fromCZ = fromBlock.getZ() >> 4;
		BlockFace face = event.getFace();
		int toCX = (fromBlock.getX() + face.getModX()) >> 4;
		int toCZ = (fromBlock.getZ() + face.getModZ()) >> 4;
		
		// If a liquid (or dragon egg) moves from one chunk to another ...
		if (toCX == fromCX && toCZ == fromCZ) return;
		
		Board board = BoardColl.get().getFixed(fromBlock.getWorld().getName().toLowerCase(), false);
		if (board == null) return;
		Map<PS, TerritoryAccess> map = board.getMapRaw();
		if (map.isEmpty()) return;
		
		PS fromPs = PS.valueOf(fromCX, fromCZ);
		PS toPs = PS.valueOf(toCX, toCZ);
		TerritoryAccess fromTa = map.get(fromPs);
		TerritoryAccess toTa = map.get(toPs);
		String fromId = fromTa != null ? fromTa.getHostFactionId() : Factions.ID_NONE;
		String toId = toTa != null ? toTa.getHostFactionId() : Factions.ID_NONE;
		
		// ... and the chunks belong to different factions ...
		if (toId.equals(fromId)) return;
		
		// ... and the faction "from" can not build at "to" ...
		Faction fromFac = FactionColl.get().getFixed(fromId);
		Faction toFac = FactionColl.get().getFixed(toId);
		if (MPerm.getPermBuild().has(fromFac, toFac)) return;
		
		// ... cancel!
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// ASSORTED BUILD AND INTERACT
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDamageEntity(EntityDamageByEntityEvent event)
	{
		// If a player ... 
		Entity edamager = MUtil.getLiableDamager(event);
		if (MUtil.isntPlayer(edamager)) return;
		Player player = (Player)edamager;
		
		// ... damages an entity which is edited on damage ...
		Entity edamagee = event.getEntity();
		if (edamagee == null) return;
		if ( ! MConf.get().entityTypesEditOnDamage.contains(edamagee.getType())) return;
		
		// ... and the player can't build there ...
		if (canPlayerBuildAt(player, PS.valueOf(edamagee.getLocation()), true)) return;
		
		// ... then cancel the event.
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		// only need to check right-clicks and physical as of MC 1.4+; good performance boost
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) return;
		
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null) return;  // clicked in air, apparently

		if ( ! canPlayerUseBlock(player, block, true))
		{
			event.setCancelled(true);
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;  // only interested on right-clicks for below

		if ( ! playerTrustedCantPlace(player, PS.valueOf(block), event.getMaterial(), true))
		{
			event.setCancelled(true);
			return;
		}
		
		if ( ! playerCanUseItemHere(player, PS.valueOf(block), event.getMaterial(), true))
		{
			event.setCancelled(true);
			return;
		}
	}

	public static boolean playerCanUseItemHere(Player player, PS ps, Material material, boolean verboose)
	{
		if (MUtil.isntPlayer(player)) return true;
		
		if ( ! MConf.get().materialsEditTools.contains(material) && ! MConf.get().materialsEditToolsDupeBug.contains(material)) return true;
		
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		MPlayer mplayer = MPlayer.get(player);
		if (mplayer.isOverriding()) return true;
		
		return MPerm.getPermBuild().has(mplayer, ps, verboose);
	}
	
	public static boolean playerTrustedCantBreak(Player player, PS ps, Material material, boolean verboose)
	{
		if (MUtil.isntPlayer(player)) return true;
		
		if ( ! MConf.get().materialsTrustCantBreak.contains(material)) return true;
		
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		MPlayer mplayer = MPlayer.get(player);
		if (mplayer.isOverriding()) return true;
		
		return MPerm.getPermTrusted().has(mplayer, ps, verboose);
	}
	
	public static boolean playerTrustedCantPlace(Player player, PS ps, Material material, boolean verboose)
	{
		if (MUtil.isntPlayer(player)) return true;
		
		if ( ! MConf.get().materialsTrustCantPlace.contains(material)) return true;
		
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		MPlayer mplayer = MPlayer.get(player);
		if (mplayer.isOverriding()) return true;
		
		return MPerm.getPermTrusted().has(mplayer, ps, verboose);
	}
	
	public static boolean canPlayerUseBlock(Player player, Block block, boolean verboose)
	{
		if (MUtil.isntPlayer(player)) return true;
		
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		MPlayer me = MPlayer.get(player);
		if (me.isOverriding()) return true;
		
		PS ps = PS.valueOf(block);
		Material material = block.getType();
		
		if (MConf.get().materialsEditOnInteract.contains(material) && ! MPerm.getPermBuild().has(me, ps, verboose)) return false;
		if (MConf.get().materialsTrustCantPlace.contains(material) && ! MPerm.getPermTrusted().has(me, ps, verboose)) return false;
		if (MConf.get().materialsTrustCantBreak.contains(material) && ! MPerm.getPermTrusted().has(me, ps, verboose)) return false;
		if (MConf.get().materialsContainer.contains(material) && ! MPerm.getPermContainer().has(me, ps, verboose)) return false;
		if (MConf.get().materialsDoor.contains(material) && ! MPerm.getPermDoor().has(me, ps, verboose)) return false;
		if (material == Material.STONE_BUTTON && ! MPerm.getPermButton().has(me, ps, verboose)) return false;
		if (material == Material.LEVER && ! MPerm.getPermLever().has(me, ps, verboose)) return false;
		return true;
	}
	
	// This event will not fire for Minecraft 1.8 armor stands.
	// Armor stands are handled in EngineSpigot instead.
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteractEntity(PlayerInteractEntityEvent event)
	{
		// Ignore Off Hand
		if (isOffHand(event)) return;
		
		// Gather Info
		final Player player = event.getPlayer();
		final Entity entity = event.getRightClicked();
		final boolean verboose = true;
		
		// If we can't use ...
		if (EngineMain.canPlayerUseEntity(player, entity, verboose)) return;
		
		// ... block use.
		event.setCancelled(true);
	}
	
	public static boolean canPlayerUseEntity(Player player, Entity entity, boolean verboose)
	{
		// If a player ...
		if (MUtil.isntPlayer(player)) return true;
		
		// ... interacts with an entity ...
		if (entity == null) return true;
		EntityType type = entity.getType();
		PS ps = PS.valueOf(entity.getLocation());
		
		// ... and the player does not bypass all protections ...
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		// ... and the player is not using admin mode ...
		MPlayer me = MPlayer.get(player);
		if (me.isOverriding()) return true;
		
		// ... check container entity rights ...
		if (MConf.get().entityTypesContainer.contains(type) && ! MPerm.getPermContainer().has(me, ps, verboose)) return false;
		
		// ... check build entity rights ...
		if (MConf.get().entityTypesEditOnInteract.contains(type) && ! MPerm.getPermBuild().has(me, ps, verboose)) return false;
		
		// ... otherwise we may use the entity.
		return true;
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();
		
		if (playerCanUseItemHere(player, PS.valueOf(block), event.getBucket(), true)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (playerCanUseItemHere(player, PS.valueOf(block), event.getBucket(), true)) return;
		
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// TELEPORT TO HOME ON DEATH
	// -------------------------------------------- //
	
	public void teleportToHomeOnDeath(PlayerRespawnEvent event, EventPriority priority)
	{
		// If a player is respawning ...
		final Player player = event.getPlayer();
		if (MUtil.isntPlayer(player)) return;
		final MPlayer mplayer = MPlayer.get(player);
		
		// ... homes are enabled, active and at this priority ...
		if (!MConf.get().homesEnabled) return;
		if (!MConf.get().homesTeleportToOnDeathActive) return;
		if (MConf.get().homesTeleportToOnDeathPriority != priority) return;
		
		// ... and the player has a faction ...
		final Faction faction = mplayer.getFaction();
		if (faction.isNone()) return;
		
		// ... and the faction has a home ...
		PS home = faction.getHome();
		if (home == null) return;
		
		// ... and the home is translatable ...
		Location respawnLocation = null;
		try
		{
			respawnLocation = home.asBukkitLocation(true);
		}
		catch (Exception e)
		{
			// The home location map may have been deleted
			return;
		}
		
		// ... then use it for the respawn location.
		event.setRespawnLocation(respawnLocation);
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void teleportToHomeOnDeathLowest(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.LOWEST);
	}
	
	@EventHandler(priority = EventPriority.LOW)
	public void teleportToHomeOnDeathLow(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.LOW);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void teleportToHomeOnDeathNormal(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.NORMAL);
	}
	
	@EventHandler(priority = EventPriority.HIGH)
	public void teleportToHomeOnDeathHigh(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.HIGH);
	}
	
	@EventHandler(priority = EventPriority.HIGHEST)
	public void teleportToHomeOnDeathHighest(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.HIGHEST);
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void teleportToHomeOnDeathMonitor(PlayerRespawnEvent event)
	{
		this.teleportToHomeOnDeath(event, EventPriority.MONITOR);
	}
	
}
