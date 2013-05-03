package com.massivecraft.factions.listeners;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Enderman;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
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

import com.massivecraft.factions.Const;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Factions;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.TerritoryAccess;
import com.massivecraft.factions.entity.BoardColls;
import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.MConf;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayerColl;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.factions.event.FactionsEventPowerChange;
import com.massivecraft.factions.event.FactionsEventPowerChange.PowerChangeReason;
import com.massivecraft.factions.util.VisualizeUtil;
import com.massivecraft.mcore.ps.PS;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.util.Txt;

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
		
		// Check Disabled
		if (UConf.isDisabled(player)) return;
		
		// ... gather info on the player and the move ...
		UPlayer uplayer = UPlayerColls.get().get(event.getTo()).get(player);
		
		PS chunkFrom = PS.valueOf(event.getFrom()).getChunk(true);
		PS chunkTo = PS.valueOf(event.getTo()).getChunk(true);
		
		Faction factionFrom = BoardColls.get().getFactionAt(chunkFrom);
		Faction factionTo = BoardColls.get().getFactionAt(chunkTo);
		
		// ... and send info onwards.
		this.chunkChangeTerritoryInfo(uplayer, player, chunkFrom, chunkTo, factionFrom, factionTo);
		this.chunkChangeAutoClaim(uplayer, chunkTo);
	}
	
	// -------------------------------------------- //
	// CHUNK CHANGE: TERRITORY INFO
	// -------------------------------------------- //
	
	public void chunkChangeTerritoryInfo(UPlayer uplayer, Player player, PS chunkFrom, PS chunkTo, Faction factionFrom, Faction factionTo)
	{
		// send host faction info updates
		if (uplayer.isMapAutoUpdating())
		{
			uplayer.sendMessage(BoardColls.get().getMap(uplayer, chunkTo, player.getLocation().getYaw()));
		}
		else if (factionFrom != factionTo)
		{
			String msg = Txt.parse("<i>") + " ~ " + factionTo.getName(uplayer);
			if (factionTo.hasDescription())
			{
				msg += " - " + factionTo.getDescription();
			}
			player.sendMessage(msg);
		}

		// Show access level message if it changed.
		TerritoryAccess accessFrom = BoardColls.get().getTerritoryAccessAt(chunkFrom);
		Boolean hasTerritoryAccessFrom = accessFrom.hasTerritoryAccess(uplayer);
		
		TerritoryAccess accessTo = BoardColls.get().getTerritoryAccessAt(chunkTo);
		Boolean hasTerritoryAccessTo = accessTo.hasTerritoryAccess(uplayer);
		
		if (!MUtil.equals(hasTerritoryAccessFrom, hasTerritoryAccessTo))
		{
			if (hasTerritoryAccessTo == null)
			{
				uplayer.msg("<i>You have standard access to this area.");
			}
			else if (hasTerritoryAccessTo)
			{
				uplayer.msg("<g>You have elevated access to this area.");
			}
			else
			{
				uplayer.msg("<b>You have decreased access to this area.");
			}
		}
	}
	
	// -------------------------------------------- //
	// CHUNK CHANGE: AUTO CLAIM
	// -------------------------------------------- //
	
	public void chunkChangeAutoClaim(UPlayer uplayer, PS chunkTo)
	{
		// If the player is auto claiming ...
		Faction autoClaimFaction = uplayer.getAutoClaimFaction();
		if (autoClaimFaction == null) return;
		
		// ... try claim.
		uplayer.tryClaim(autoClaimFaction, chunkTo, true, true);
	}
	
	// -------------------------------------------- //
	// POWER LOSS ON DEATH
	// -------------------------------------------- //
	
	@EventHandler(priority = EventPriority.NORMAL)
	public void powerLossOnDeath(PlayerDeathEvent event)
	{
		// If a player dies ...
		Player player = event.getEntity();
		
		// Check Disabled
		if (UConf.isDisabled(player)) return;
		
		UPlayer uplayer = UPlayer.get(player);
		
		// ... and powerloss can happen here ...
		Faction faction = BoardColls.get().getFactionAt(PS.valueOf(player));
		
		if (!faction.getFlag(FFlag.POWERLOSS))
		{
			uplayer.msg("<i>You didn't lose any power since the territory you died in works that way.");
			return;
		}
		
		if (MConf.get().worldsNoPowerLoss.contains(player.getWorld().getName()))
		{
			uplayer.msg("<i>You didn't lose any power due to the world you died in.");
			return;
		}
		
		// ... alter the power ...
		double newPower = uplayer.getPower() + uplayer.getPowerPerDeath();
		
		FactionsEventPowerChange powerChangeEvent = new FactionsEventPowerChange(null, uplayer, PowerChangeReason.DEATH, newPower);
		powerChangeEvent.run();
		if (powerChangeEvent.isCancelled()) return;
		newPower = powerChangeEvent.getNewPower();
		
		uplayer.setPower(newPower);
		
		// ... and inform the player.
		// TODO: A progress bar here would be epic :)
		uplayer.msg("<i>Your power is now <h>%.2f / %.2f", newPower, uplayer.getPowerMax());
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
		EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0);
		if (this.canCombatDamageHappen(sub, false)) return;
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void canCombatDamageHappen(PotionSplashEvent event)
	{
		// If a harmful potion is splashing ...
		if (!MUtil.isHarmfulPotion(event.getPotion())) return;
		
		Entity thrower = event.getPotion().getShooter();

		// ... scan through affected entities to make sure they're all valid targets.
		for (LivingEntity affectedEntity : event.getAffectedEntities())
		{
			EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(thrower, affectedEntity, EntityDamageEvent.DamageCause.CUSTOM, 0);
			if (this.canCombatDamageHappen(sub, true)) continue;
			
			// affected entity list doesn't accept modification (iter.remove() is a no-go), but this works
			event.setIntensity(affectedEntity, 0.0);
		}
	}

	public boolean canCombatDamageHappen(EntityDamageByEntityEvent event, boolean notify)
	{	
		// If the defender is a player ...
		Entity edefender = event.getEntity();
		if (!(edefender instanceof Player)) return true;
		Player defender = (Player)edefender;
		UPlayer fdefender = UPlayer.get(edefender);
		
		// Check Disabled
		if (UConf.isDisabled(defender)) return true;
		
		// ... and the attacker is someone else ...
		Entity eattacker = event.getDamager();
		if (eattacker instanceof Projectile)
		{
			eattacker = ((Projectile)eattacker).getShooter();
		}
		if (eattacker.equals(edefender)) return true;
		
		// ... gather defender PS and faction information ...
		PS defenderPs = PS.valueOf(defender);
		Faction defenderPsFaction = BoardColls.get().getFactionAt(defenderPs);
		
		// ... PVP flag may cause a damage block ...
		if (defenderPsFaction.getFlag(FFlag.PVP) == false)
		{
			if (eattacker instanceof Player)
			{
				if (notify)
				{
					UPlayer attacker = UPlayer.get(eattacker);
					attacker.msg("<i>PVP is disabled in %s.", defenderPsFaction.describeTo(attacker));
				}
				return false;
			}
			return defenderPsFaction.getFlag(FFlag.MONSTERS);
		}

		// ... and if the attacker is a player ...
		if (!(eattacker instanceof Player)) return true;
		Player attacker = (Player)eattacker;
		UPlayer fattacker = UPlayer.get(attacker);
		
		// ... does this player bypass all protection? ...
		if (MConf.get().playersWhoBypassAllProtection.contains(attacker.getName())) return true;

		// ... gather attacker PS and faction information ...
		PS attackerPs = PS.valueOf(attacker);
		Faction attackerPsFaction = BoardColls.get().getFactionAt(attackerPs);

		// ... PVP flag may cause a damage block ...
		// (just checking the defender as above isn't enough. What about the attacker? It could be in a no-pvp area)
		// NOTE: This check is probably not that important but we could keep it anyways.
		if (attackerPsFaction.getFlag(FFlag.PVP) == false)
		{
			if (notify) fattacker.msg("<i>PVP is disabled in %s.", attackerPsFaction.describeTo(fattacker));
			return false;
		}

		// ... are PVP rules completely ignored in this world? ...
		if (MConf.get().worldsIgnorePvP.contains(defenderPs.getWorld())) return true;

		Faction defendFaction = fdefender.getFaction();
		Faction attackFaction = fattacker.getFaction();
		UConf uconf = UConf.get(attackFaction);

		if (attackFaction.isNone() && uconf.disablePVPForFactionlessPlayers)
		{
			if (notify) fattacker.msg("<i>You can't hurt other players until you join a faction.");
			return false;
		}
		else if (defendFaction.isNone())
		{
			if (defenderPsFaction == attackFaction && uconf.enablePVPAgainstFactionlessInAttackersLand)
			{
				// Allow PVP vs. Factionless in attacker's faction territory
				return true;
			}
			else if (uconf.disablePVPForFactionlessPlayers)
			{
				if (notify) fattacker.msg("<i>You can't hurt players who are not currently in a faction.");
				return false;
			}
		}

		Rel relation = defendFaction.getRelationTo(attackFaction);

		// Check the relation
		if (fdefender.hasFaction() && relation.isFriend() && defenderPsFaction.getFlag(FFlag.FRIENDLYFIRE) == false)
		{
			if (notify) fattacker.msg("<i>You can't hurt %s<i>.", relation.getDescPlayerMany());
			return false;
		}

		// You can not hurt neutrals in their own territory.
		boolean ownTerritory = fdefender.isInOwnTerritory();
		if (fdefender.hasFaction() && ownTerritory && relation == Rel.NEUTRAL)
		{
			if (notify)
			{
				fattacker.msg("<i>You can't hurt %s<i> in their own territory unless you declare them as an enemy.", fdefender.describeTo(fattacker));
				fdefender.msg("%s<i> tried to hurt you.", fattacker.describeTo(fdefender, true));
			}
			return false;
		}

		// Damage will be dealt. However check if the damage should be reduced.
		int damage = event.getDamage();
		if (damage > 0.0 && fdefender.hasFaction() && ownTerritory && uconf.territoryShieldFactor > 0)
		{
			int newDamage = (int)Math.ceil(damage * (1D - uconf.territoryShieldFactor));
			event.setDamage(newDamage);

			// Send message
			if (notify)
			{
				String perc = MessageFormat.format("{0,number,#%}", (uconf.territoryShieldFactor)); // TODO does this display correctly??
				fdefender.msg("<i>Enemy damage reduced by <rose>%s<i>.", perc);
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
		if (!event.getReason().equals("Banned by admin.")) return;
		
		// ... and we remove player data when banned ...
		if (!MConf.get().removePlayerDataWhenBanned) return;
		
		// ... get rid of their stored info.
		for (UPlayerColl coll : UPlayerColls.get().getColls())
		{
			UPlayer uplayer = coll.get(player, false);
			if (uplayer == null) continue;
			
			if (uplayer.getRole() == Rel.LEADER)
			{
				uplayer.getFaction().promoteNewLeader();
			}
			uplayer.leave();
			uplayer.detach();
		}
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
		
		// Check Disabled
		if (UConf.isDisabled(player)) return;
		
		UPlayer uplayer = UPlayer.get(player);
		
		// ... and the player does not have adminmode ...
		if (uplayer.isUsingAdminMode()) return;
		
		// ... clean up the command ...
		String command = event.getMessage();
		command = Txt.removeLeadingCommandDust(command);
		command = command.toLowerCase();
		command = command.trim();
		
		// ... the command may be denied for members of permanent factions ...
		if (uplayer.hasFaction() && uplayer.getFaction().getFlag(FFlag.PERMANENT) && containsCommand(command, UConf.get(player).denyCommandsPermanentFactionMember))
		{
			uplayer.msg("<b>You can't use \"<h>/%s<b>\" as member of a permanent faction.", command);
			event.setCancelled(true);
			return;
		}
		
		// ... if there is a faction at the players location ...
		PS ps = PS.valueOf(player).getChunk(true);
		Faction factionAtPs = BoardColls.get().getFactionAt(ps);
		if (factionAtPs.isNone()) return;
		
		// ... the command may be denied in the territory of this relation type ...
		Rel rel = factionAtPs.getRelationTo(uplayer);
		
		List<String> deniedCommands = UConf.get(player).denyCommandsTerritoryRelation.get(rel);
		if (deniedCommands == null) return;
		if (!containsCommand(command, deniedCommands)) return;
		
		uplayer.msg("<b>You can't use \"<h>/%s<b>\" in %s territory.", Txt.getNicedEnum(rel), command);
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
		if ( ! Const.ENTITY_TYPES_MONSTERS.contains(event.getEntityType())) return;
		
		// Check Disabled
		if (UConf.isDisabled(event.getLocation())) return;
		
		// ... at a place where monsters are forbidden ...
		PS ps = PS.valueOf(event.getLocation());
		Faction faction = BoardColls.get().getFactionAt(ps);
		if (faction.getFlag(FFlag.MONSTERS)) return;
		
		// ... block the spawn.
		event.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockMonsters(EntityTargetEvent event)
	{
		// If a monster ...
		if ( ! Const.ENTITY_TYPES_MONSTERS.contains(event.getEntityType())) return;
		
		// ... is targeting something ...
		Entity target = event.getTarget();
		if (target == null) return;
		
		// Check Disabled
		if (UConf.isDisabled(target)) return;
		
		// ... at a place where monsters are forbidden ...
		PS ps = PS.valueOf(target);
		Faction faction = BoardColls.get().getFactionAt(ps);
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
		
		// Check Disabled
		if (UConf.isDisabled(entity)) return;
	
		// ... and the faction there has explosions disabled ...
		Faction faction = BoardColls.get().getFactionAt(PS.valueOf(entity));
		if (faction.getFlag(FFlag.EXPLOSIONS)) return;
		
		// ... then cancel.
		event.setCancelled(true);
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityExplodeEvent event)
	{
		// If an explosion occurs at a location ...
		Location location = event.getLocation();
		
		// Check Disabled
		if (UConf.isDisabled(location)) return;
		
		// Check the entity. Are explosions disabled there? 
		if (BoardColls.get().getFactionAt(PS.valueOf(location)).getFlag(FFlag.EXPLOSIONS) == false)
		{
			event.setCancelled(true);
			return;
		}
		
		// Individually check the flag state for each block
		Iterator<Block> iter = event.blockList().iterator();
		while (iter.hasNext())
		{
			Block block = iter.next();
			Faction faction = BoardColls.get().getFactionAt(PS.valueOf(block));
			if (faction.getFlag(FFlag.EXPLOSIONS) == false) iter.remove();
		}
	}
	
	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void blockExplosion(EntityChangeBlockEvent event)
	{
		// If a wither is changing a block ...
		Entity entity = event.getEntity();
		if (!(entity instanceof Wither)) return;
		
		// Check Disabled
		if (UConf.isDisabled(entity)) return;

		// ... and the faction there has explosions disabled ...
		PS ps = PS.valueOf(event.getBlock());
		Faction faction = BoardColls.get().getFactionAt(ps);
		if (faction.getFlag(FFlag.EXPLOSIONS)) return;
		
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
		
		// Check Disabled
		if (UConf.isDisabled(entity)) return;
		
		// ... and the faction there has endergrief disabled ...
		PS ps = PS.valueOf(event.getBlock());
		Faction faction = BoardColls.get().getFactionAt(ps);
		if (faction.getFlag(FFlag.ENDERGRIEF)) return;
		
		// ... stop the block alteration.
		event.setCancelled(true);
	}

	// -------------------------------------------- //
	// FLAG: FIRE SPREAD
	// -------------------------------------------- //
	
	public void blockFireSpread(Block block, Cancellable cancellable)
	{
		// Check Disabled
		if (UConf.isDisabled(block)) return;
		
		// If the faction at the block has firespread disabled ...
		PS ps = PS.valueOf(block);
		Faction faction = BoardColls.get().getFactionAt(ps);
			
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
		if (event.getNewState().getTypeId() != 51) return;
		
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

	public static boolean canPlayerBuildAt(Player player, PS ps, boolean verboose)
	{
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		UPlayer uplayer = UPlayer.get(player);
		if (uplayer.isUsingAdminMode()) return true;

		if (!FPerm.BUILD.has(uplayer, ps, false) && FPerm.PAINBUILD.has(uplayer, ps, false))
		{
			if (verboose)
			{
				Faction hostFaction = BoardColls.get().getFactionAt(ps);
				uplayer.msg("<b>It is painful to build in the territory of %s<b>.", hostFaction.describeTo(uplayer));
				player.damage(UConf.get(player).actionDeniedPainAmount);
			}
			return true;
		}
		
		return FPerm.BUILD.has(uplayer, ps, verboose);
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

		if ( ! canPlayerBuildAt((Player)breaker, PS.valueOf(event.getEntity()), true))
		{
			event.setCancelled(true);
		}
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

		Faction pistonFaction = BoardColls.get().getFactionAt(PS.valueOf(block));

		// target end-of-the-line empty (air) block which is being pushed into, including if piston itself would extend into air
		Block targetBlock = block.getRelative(event.getDirection(), event.getLength() + 1);

		// members of faction might not have build rights in their own territory, but pistons should still work regardless; so, address that corner case
		Faction targetFaction = BoardColls.get().getFactionAt(PS.valueOf(targetBlock));
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

		Faction pistonFaction = BoardColls.get().getFactionAt(PS.valueOf(event.getBlock()));

		// members of faction might not have build rights in their own territory, but pistons should still work regardless; so, address that corner case
		Faction targetFaction = BoardColls.get().getFactionAt(retractPs);
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
		if (!Const.MATERIALS_EDIT_TOOLS.contains(material)) return true;
		
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		UPlayer uplayer = UPlayer.get(player);
		if (uplayer.isUsingAdminMode()) return true;
		
		return FPerm.BUILD.has(uplayer, ps, !justCheck);
	}
	
	public static boolean canPlayerUseBlock(Player player, Block block, boolean justCheck)
	{
		String name = player.getName();
		if (MConf.get().playersWhoBypassAllProtection.contains(name)) return true;

		UPlayer me = UPlayer.get(player);
		if (me.isUsingAdminMode()) return true;
		
		PS ps = PS.valueOf(block);
		Material material = block.getType();
		
		if (Const.MATERIALS_EDIT_ON_INTERACT.contains(material) && ! FPerm.BUILD.has(me, ps, ! justCheck)) return false;
		if (Const.MATERIALS_CONTAINER.contains(material) && ! FPerm.CONTAINER.has(me, ps, ! justCheck)) return false;
		if (Const.MATERIALS_DOOR.contains(material) && ! FPerm.DOOR.has(me, ps, ! justCheck)) return false;
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
	
}
