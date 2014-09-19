package com.massivecraft.factions.listeners;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.BlockIgniteEvent.IgniteCause;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityCombustByEntityEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.PotionSplashEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.projectiles.ProjectileSource;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.BoardColl;
import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.factions.event.EventFactionsPvpDisallowed;
import com.massivecraft.factions.event.EventFactionsPowerChange;
import com.massivecraft.factions.event.EventFactionsPowerChange.PowerChangeReason;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.massivecore.ps.PS;
import com.massivecraft.massivecore.util.MUtil;
import com.massivecraft.massivecore.util.PlayerUtil;
import com.massivecraft.massivecore.util.Txt;

public class FactionsListenerMain implements Listener
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static FactionsListenerMain i = new FactionsListenerMain();
	public static FactionsListenerMain get() { return i; }
	public FactionsListenerMain() {}
	
	// -------------------------------------------- //
	// SETUP
	// -------------------------------------------- //
	
	public void setup()
	{
		Bukkit.getPluginManager().registerEvents(this, Factions.get());
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
			mplayer.sendMessage(BoardColl.get().getMap(mplayer, chunkTo, player.getLocation().getYaw()));
		}
		else if (factionFrom != factionTo)
		{
			String msg = Txt.parse("<i>") + " ~ " + factionTo.getName(mplayer);
			if (factionTo.hasDescription())
			{
				msg += " - " + factionTo.getDescription();
			}
			player.sendMessage(msg);
		}

		// Show access level message if it changed.
		TerritoryAccess accessFrom = BoardColl.get().getTerritoryAccessAt(chunkFrom);
		Boolean hasTerritoryAccessFrom = accessFrom.hasTerritoryAccess(mplayer);
		
		TerritoryAccess accessTo = BoardColl.get().getTerritoryAccessAt(chunkTo);
		Boolean hasTerritoryAccessTo = accessTo.hasTerritoryAccess(mplayer);
		
		if (!MUtil.equals(hasTerritoryAccessFrom, hasTerritoryAccessTo))
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
	
	// -------------------------------------------- //
	// CHUNK CHANGE: AUTO CLAIM
	// -------------------------------------------- //
	
	public void chunkChangeAutoClaim(MPlayer mplayer, PS chunkTo)
	{
		// If the player is auto claiming ...
		Faction autoClaimFaction = mplayer.getAutoClaimFaction();
		if (autoClaimFaction == null) return;
		
		// ... try claim.
		mplayer.tryClaim(autoClaimFaction, chunkTo, true, true);
	}
	
	// -------------------------------------------- //
	// POWER LOSS ON DEATH
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void powerLossOnDeath(PlayerDeathEvent event)
	{
		// If a player dies ...
		Player player = event.getEntity();
		
		// ... and this is the first death event this tick ...
		// (yeah other plugins can case death event to fire twice the same tick)
		if (PlayerUtil.isDuplicateDeathEvent(event)) return;
		
		MPlayer mplayer = MPlayer.get(player);
		
		// ... and powerloss can happen here ...
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(player));
		
		if (!faction.getFlag(FFlag.POWERLOSS))
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
	// CAN COMBAT DAMAGE HAPPEN
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canCombatDamageHappen(EntityDamageEvent event)
	{
		// TODO: Can't we just listen to the class type the sub is of?
		if (!(event instanceof EntityDamageByEntityEvent)) return;
		EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent)event;
		
		if (this.canCombatDamageHappen(sub, true)) return;
		event.setCancelled(true);
	}

	// mainly for flaming arrows; don't want allies or people in safe zones to be ignited even after damage event is cancelled
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canCombatDamageHappen(EntityCombustByEntityEvent event)
	{
		EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0D);
		if (this.canCombatDamageHappen(sub, false)) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canCombatDamageHappen(PotionSplashEvent event)
	{
		// If a harmful potion is splashing ...
		if (!MUtil.isHarmfulPotion(event.getPotion())) return;
		
		ProjectileSource projectileSource = event.getPotion().getShooter();
		if (! (projectileSource instanceof Entity)) return;
		
		Entity thrower = (Entity)projectileSource;

		// ... scan through affected entities to make sure they're all valid targets.
		for (LivingEntity affectedEntity : event.getAffectedEntities())
		{
			EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(thrower, affectedEntity, EntityDamageEvent.DamageCause.CUSTOM, 0D);
			if (this.canCombatDamageHappen(sub, true)) continue;
			
			// affected entity list doesn't accept modification (iter.remove() is a no-go), but this works
			event.setIntensity(affectedEntity, 0.0);
		}
	}

	// Utility method used in "canCombatDamageHappen" below.
	public static boolean falseUnlessDisallowedPvpEventCancelled(Player attacker, Player defender, EntityDamageByEntityEvent event)
	{
		EventFactionsPvpDisallowed dpe = new EventFactionsPvpDisallowed(attacker, defender, event);
		dpe.run();
		return dpe.isCancelled();
	}
	
	public boolean canCombatDamageHappen(EntityDamageByEntityEvent event, boolean notify)
	{
		boolean ret = true;
		
		// If the defender is a player ...
		Entity edefender = event.getEntity();
		if (!(edefender instanceof Player)) return true;
		Player defender = (Player)edefender;
		MPlayer udefender = MPlayer.get(edefender);
		
		// ... and the attacker is someone else ...
		Entity eattacker = MUtil.getLiableDamager(event);
		
		// (we check null here since there may not be an attacker)
		// (lack of attacker situations can be caused by other bukkit plugins)
		if (eattacker != null && eattacker.equals(edefender)) return true;
		
		// ... gather defender PS and faction information ...
		PS defenderPs = PS.valueOf(defender);
		Faction defenderPsFaction = BoardColl.get().getFactionAt(defenderPs);
		
		// ... PVP flag may cause a damage block ...
		if (defenderPsFaction.getFlag(FFlag.PVP) == false)
		{
			if (eattacker == null)
			{
				// No attacker?
				// Let's behave as if it were a player
				return falseUnlessDisallowedPvpEventCancelled(null, defender, event);
			}
			if (eattacker instanceof Player)
			{
				ret = falseUnlessDisallowedPvpEventCancelled((Player)eattacker, defender, event);
				if (!ret && notify)
				{
					MPlayer attacker = MPlayer.get(eattacker);
					attacker.msg("<i>PVP is disabled in %s.", defenderPsFaction.describeTo(attacker));
				}
				return ret;
			}
			return defenderPsFaction.getFlag(FFlag.MONSTERS);
		}

		// ... and if the attacker is a player ...
		if (!(eattacker instanceof Player)) return true;
		Player attacker = (Player)eattacker;
		MPlayer uattacker = MPlayer.get(attacker);
		
		// ... does this player bypass all protection? ...
		if (MConf.get().playersWhoBypassAllProtection.contains(attacker.getName())) return true;

		// ... gather attacker PS and faction information ...
		PS attackerPs = PS.valueOf(attacker);
		Faction attackerPsFaction = BoardColl.get().getFactionAt(attackerPs);

		// ... PVP flag may cause a damage block ...
		// (just checking the defender as above isn't enough. What about the attacker? It could be in a no-pvp area)
		// NOTE: This check is probably not that important but we could keep it anyways.
		if (attackerPsFaction.getFlag(FFlag.PVP) == false)
		{
			ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
			if (!ret && notify) uattacker.msg("<i>PVP is disabled in %s.", attackerPsFaction.describeTo(uattacker));
			return ret;
		}

		// ... are PVP rules completely ignored in this world? ...
		if (!MConf.get().worldsPvpRulesEnabled.contains(defenderPs.getWorld())) return true;

		Faction defendFaction = udefender.getFaction();
		Faction attackFaction = uattacker.getFaction();

		if (attackFaction.isNone() && MConf.get().disablePVPForFactionlessPlayers)
		{
			ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
			if (!ret && notify) uattacker.msg("<i>You can't hurt other players until you join a faction.");
			return ret;
		}
		else if (defendFaction.isNone())
		{
			if (defenderPsFaction == attackFaction && MConf.get().enablePVPAgainstFactionlessInAttackersLand)
			{
				// Allow PVP vs. Factionless in attacker's faction territory
				return true;
			}
			else if (MConf.get().disablePVPForFactionlessPlayers)
			{
				ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
				if (!ret && notify) uattacker.msg("<i>You can't hurt players who are not currently in a faction.");
				return ret;
			}
		}

		Rel relation = defendFaction.getRelationTo(attackFaction);

		// Check the relation
		if (udefender.hasFaction() && relation.isFriend() && defenderPsFaction.getFlag(FFlag.FRIENDLYFIRE) == false)
		{
			ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
			if (!ret && notify) uattacker.msg("<i>You can't hurt %s<i>.", relation.getDescPlayerMany());
			return ret;
		}

		// You can not hurt neutrals in their own territory.
		boolean ownTerritory = udefender.isInOwnTerritory();
		
		if (udefender.hasFaction() && ownTerritory && relation == Rel.NEUTRAL)
		{
			ret = falseUnlessDisallowedPvpEventCancelled(attacker, defender, event);
			if (!ret && notify)
			{
				uattacker.msg("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy.", udefender.describeTo(uattacker));
				udefender.msg("%s<i> tried to hurt you.", uattacker.describeTo(udefender, true));
			}
			return ret;
		}

		// Damage will be dealt. However check if the damage should be reduced.
		double damage = event.getDamage();
		if (damage > 0.0 && udefender.hasFaction() && ownTerritory && MConf.get().territoryShieldFactor > 0)
		{
			double newDamage = damage * (1D - MConf.get().territoryShieldFactor);
			event.setDamage(newDamage);

			// Send message
			if (notify)
			{
				String perc = MessageFormat.format("{0,number,#%}", (MConf.get().territoryShieldFactor)); // TODO does this display correctly??
				udefender.msg("<i>Enemy damage reduced by <rose>%s<i>.", perc);
			}
		}

		return true;
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
		if (!MConf.get().removePlayerDataWhenBanned) return;
		
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
		
		MPlayer mplayer = MPlayer.get(player);
		
		// ... and the player does not have adminmode ...
		if (mplayer.isUsingAdminMode()) return;
		
		// ... clean up the command ...
		String command = event.getMessage();
		command = Txt.removeLeadingCommandDust(command);
		command = command.toLowerCase();
		command = command.trim();
		
		// ... the command may be denied for members of permanent factions ...
		if (mplayer.hasFaction() && mplayer.getFaction().getFlag(FFlag.PERMANENT) && containsCommand(command, MConf.get().denyCommandsPermanentFactionMember))
		{
			mplayer.msg("<b>You can't use \"<h>/%s<b>\" as member of a permanent faction.", command);
			event.setCancelled(true);
			return;
		}
		
		// ... if there is a faction at the players location ...
		PS ps = PS.valueOf(player).getChunk(true);
		Faction factionAtPs = BoardColl.get().getFactionAt(ps);
		if (factionAtPs.isNone()) return; // TODO: An NPE can arise here? Why?
		
		// ... the command may be denied in the territory of this relation type ...
		Rel rel = factionAtPs.getRelationTo(mplayer);
		
		List<String> deniedCommands = MConf.get().denyCommandsTerritoryRelation.get(rel);
		if (deniedCommands == null) return;
		if (!containsCommand(command, deniedCommands)) return;
		
		mplayer.msg("<b>You can't use \"<h>/%s<b>\" in %s territory.", command, Txt.getNicedEnum(rel));
		event.setCancelled(true);
	}

	private static boolean containsCommand(String needle, Collection<String> haystack)
	{
		if (needle == null) return false;
		needle = Txt.removeLeadingCommandDust(needle);
		needle = needle.toLowerCase();
		
		for (String straw : haystack)
		{
			if (straw == null) continue;
			straw = Txt.removeLeadingCommandDust(straw);
			straw = straw.toLowerCase();
			
			if (needle.startsWith(straw)) return true;
		}
		
		return false;
	}
	
	// -------------------------------------------- //
	// FLAG: MONSTERS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockMonsters(CreatureSpawnEvent event)
	{
		// If a monster is spawning ...
		if ( ! MConf.get().entityTypesMonsters.contains(event.getEntityType())) return;
		
		// ... at a place where monsters are forbidden ...
		PS ps = PS.valueOf(event.getLocation());
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction.getFlag(FFlag.MONSTERS)) return;
		
		// ... block the spawn.
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockMonsters(EntityTargetEvent event)
	{
		// If a monster ...
		if ( ! MConf.get().entityTypesMonsters.contains(event.getEntityType())) return;
		
		// ... is targeting something ...
		Entity target = event.getTarget();
		if (target == null) return;
		
		// ... at a place where monsters are forbidden ...
		PS ps = PS.valueOf(target);
		Faction faction = BoardColl.get().getFactionAt(ps);
		if (faction.getFlag(FFlag.MONSTERS)) return;
		
		// ... then if ghast target nothing ...
		if (event.getEntityType() == EntityType.GHAST)
		{
			event.setTarget(null);
			return;
		}
		
		// ... otherwise simply cancel.
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// FLAG: EXPLOSIONS
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(HangingBreakEvent event)
	{
		// If a hanging entity was broken by an explosion ...
		if (event.getCause() != RemoveCause.EXPLOSION) return;
		Entity entity = event.getEntity();
	
		// ... and the faction there has explosions disabled ...
		Faction faction = BoardColl.get().getFactionAt(PS.valueOf(entity));
		if (faction.isExplosionsAllowed()) return;
		
		// ... then cancel.
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
		if (faction.getFlag(FFlag.ENDERGRIEF)) return;
		
		// ... stop the block alteration.
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
			
		if (faction.getFlag(FFlag.FIRESPREAD)) return;
		
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

		if (mplayer.isUsingAdminMode()) return true;

		if (!FPerm.BUILD.has(mplayer, ps, false) && FPerm.PAINBUILD.has(mplayer, ps, false))
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
		
		return FPerm.BUILD.has(mplayer, ps, verboose);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(HangingPlaceEvent event)
	{
		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getEntity()), true)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(HangingBreakEvent event)
	{
		if (! (event instanceof HangingBreakByEntityEvent)) return;
		HangingBreakByEntityEvent entityEvent = (HangingBreakByEntityEvent)event;
		
		Entity breaker = entityEvent.getRemover();
		if (! (breaker instanceof Player)) return;

		if ( ! canPlayerBuildAt(breaker, PS.valueOf(event.getEntity()), true))
		{
			event.setCancelled(true);
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void itemFrameDamage(EntityDamageByEntityEvent event)
	{
		// If the damagee is an ItemFrame ...
		Entity edamagee = event.getEntity();
		if (!(edamagee instanceof ItemFrame)) return;
		ItemFrame itemFrame = (ItemFrame)edamagee;
		
		// ... and the liable damager is a player ...
		Entity edamager = MUtil.getLiableDamager(event);
		if (!(edamager instanceof Player)) return;
		Player player = (Player)edamager;
		
		// ... and the player can't build there ...
		if (canPlayerBuildAt(player, PS.valueOf(itemFrame), true)) return;
		
		// ... then cancel the event.
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void blockBuild(BlockPlaceEvent event)
	{
		if (!event.canBuild()) return;

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), true)) return;
		
		event.setBuild(false);
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockBreakEvent event)
	{
		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), true)) return;
		
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockDamageEvent event)
	{
		if (!event.getInstaBreak()) return;

		if (canPlayerBuildAt(event.getPlayer(), PS.valueOf(event.getBlock()), true)) return;
		
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockBuild(BlockPistonExtendEvent event)
	{
		Block block = event.getBlock();

		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(block));

		// target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);

		// members of faction might not have build rights in their own territory, but pistons should still work regardless; so, address that corner case
		Faction targetFaction = BoardColl.get().getFactionAt(PS.valueOf(targetBlock));
		if (targetFaction == pistonFaction) return;

		// if potentially pushing into air/water/lava in another territory, we need to check it out
		if ((targetBlock.isEmpty() || targetBlock.isLiquid()) && ! FPerm.BUILD.has(pistonFaction, targetFaction))
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
		// if not a sticky piston, retraction should be fine
		if (!event.isSticky()) return;

		Block retractBlock = event.getRetractLocation().getBlock();
		PS retractPs = PS.valueOf(retractBlock);

		// if potentially retracted block is just air/water/lava, no worries
		if (retractBlock.isEmpty() || retractBlock.isLiquid()) return;

		Faction pistonFaction = BoardColl.get().getFactionAt(PS.valueOf(event.getBlock()));

		// members of faction might not have build rights in their own territory, but pistons should still work regardless; so, address that corner case
		Faction targetFaction = BoardColl.get().getFactionAt(retractPs);
		if (targetFaction == pistonFaction) return;

		if (!FPerm.BUILD.has(pistonFaction, targetFaction))
		{
			event.setCancelled(true);
		}
	}
	
	// -------------------------------------------- //
	// ASSORTED BUILD AND INTERACT
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerInteract(PlayerInteractEvent event)
	{
		// only need to check right-clicks and physical as of MC 1.4+; good performance boost
		if (event.getAction() != Action.RIGHT_CLICK_BLOCK && event.getAction() != Action.PHYSICAL) return;

		Block block = event.getClickedBlock();
		Player player = event.getPlayer();

		if (block == null) return;  // clicked in air, apparently

		if ( ! canPlayerUseBlock(player, block, false))
		{
			event.setCancelled(true);
			return;
		}

		if (event.getAction() != Action.RIGHT_CLICK_BLOCK) return;  // only interested on right-clicks for below

		if ( ! playerCanUseItemHere(player, PS.valueOf(block), event.getMaterial(), false))
		{
			event.setCancelled(true);
			return;
		}
	}

	// TODO: Refactor ! justCheck    -> to informIfNot
	// TODO: Possibly incorporate pain build... 
	public static boolean playerCanUseItemHere(Player player, PS ps, Material material, boolean justCheck)
	{
		if (!MConf.get().materialsEditTools.contains(material)) return true;
		
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		MPlayer mplayer = MPlayer.get(player);
		if (mplayer.isUsingAdminMode()) return true;
		
		return FPerm.BUILD.has(mplayer, ps, !justCheck);
	}
	
	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck)
	{
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		MPlayer me = MPlayer.get(player);
		if (me.isUsingAdminMode()) return true;
		
		PS ps = PS.valueOf(block);
		Material material = block.getType();
		
		if (MConf.get().materialsEditOnInteract.contains(material) && ! FPerm.BUILD.has(me, ps, ! justCheck)) return false;
		if (MConf.get().materialsContainer.contains(material) && ! FPerm.CONTAINER.has(me, ps, ! justCheck)) return false;
		if (MConf.get().materialsDoor.contains(material) && ! FPerm.DOOR.has(me, ps, ! justCheck)) return false;
		if (material == Material.STONE_BUTTON && ! FPerm.BUTTON.has(me, ps, ! justCheck)) return false;
		if (material == Material.LEVER && ! FPerm.LEVER.has(me, ps, ! justCheck)) return false;
		return true;
	}

	// For some reason onPlayerInteract() sometimes misses bucket events depending on distance (something like 2-3 blocks away isn't detected),
	// but these separate bucket events below always fire without fail
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();
		
		if (playerCanUseItemHere(player, PS.valueOf(block), event.getBucket(), false)) return;
		
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerBucketFill(PlayerBucketFillEvent event)
	{
		Block block = event.getBlockClicked();
		Player player = event.getPlayer();

		if (playerCanUseItemHere(player, PS.valueOf(block), event.getBucket(), false)) return;
		
		event.setCancelled(true);
	}
	
	// -------------------------------------------- //
	// TELEPORT TO HOME ON DEATH
	// -------------------------------------------- //
	
	public void teleportToHomeOnDeath(PlayerRespawnEvent event, EventPriority priority)
	{
		// If a player is respawning ...
		final Player player = event.getPlayer();
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
