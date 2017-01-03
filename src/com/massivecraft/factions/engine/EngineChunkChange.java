package com.massivecraft.factions.engine;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.massivecraft.factions.Rel;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.FactionColl;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MFlag;
import com.massivecraft.factions.entity.MPerm;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.event.EventFactionsChunksChange;
import com.massivecraft.massivecore.Engine;
import com.massivecraft.massivecore.collections.MassiveList;
import com.massivecraft.massivecore.collections.MassiveSet;
import com.massivecraft.massivecore.mixin.MixinWorld;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.Txt;

public class EngineChunkChange extends Engine
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static EngineChunkChange i = new EngineChunkChange();
	public static EngineChunkChange get() { return i; }

	// -------------------------------------------- //
	// CHUNK CHANGE: ALLOWED
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

}
