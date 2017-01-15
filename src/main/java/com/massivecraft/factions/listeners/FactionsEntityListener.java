package com.massivecraft.factions.listeners;

import com.massivecraft.factions.*;
import com.massivecraft.factions.event.PowerLossEvent;
import com.massivecraft.factions.struct.Relation;
import com.massivecraft.factions.util.MiscUtil;
import com.massivecraft.factions.zcore.util.TL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.TravelAgent;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.entity.minecart.ExplosiveMinecart;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.hanging.HangingBreakEvent.RemoveCause;
import org.bukkit.event.hanging.HangingPlaceEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.projectiles.ProjectileSource;

import java.util.*;


public class FactionsEntityListener implements Listener {

    public P p;

    public FactionsEntityListener(P p) {
        this.p = p;
    }

    @EventHandler(priority = EventPriority.NORMAL)
    public void onEntityDeath(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player)) {
            return;
        }

        Player player = (Player) entity;
        FPlayer fplayer = FPlayers.getInstance().getByPlayer(player);
        Faction faction = Board.getInstance().getFactionAt(new FLocation(player.getLocation()));

        PowerLossEvent powerLossEvent = new PowerLossEvent(faction, fplayer);
        // Check for no power loss conditions
        if (faction.isWarZone()) {
            // war zones always override worldsNoPowerLoss either way, thus this layout
            if (!Conf.warZonePowerLoss) {
                powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WARZONE.toString());
                powerLossEvent.setCancelled(true);
            }
            if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName())) {
                powerLossEvent.setMessage(TL.PLAYER_POWER_LOSS_WARZONE.toString());
            }
        } else if (faction.isWilderness() && !Conf.wildernessPowerLoss && !Conf.worldsNoWildernessProtection.contains(player.getWorld().getName())) {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WILDERNESS.toString());
            powerLossEvent.setCancelled(true);
        } else if (Conf.worldsNoPowerLoss.contains(player.getWorld().getName())) {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_WORLD.toString());
            powerLossEvent.setCancelled(true);
        } else if (Conf.peacefulMembersDisablePowerLoss && fplayer.hasFaction() && fplayer.getFaction().isPeaceful()) {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOLOSS_PEACEFUL.toString());
            powerLossEvent.setCancelled(true);
        } else {
            powerLossEvent.setMessage(TL.PLAYER_POWER_NOW.toString());
        }

        // call Event
        Bukkit.getPluginManager().callEvent(powerLossEvent);

        // Call player onDeath if the event is not cancelled
        if (!powerLossEvent.isCancelled()) {
            fplayer.onDeath();
        }
        // Send the message from the powerLossEvent
        final String msg = powerLossEvent.getMessage();
        if (msg != null && !msg.isEmpty()) {
            fplayer.msg(msg, fplayer.getPowerRounded(), fplayer.getPowerMaxRounded());
        }
    }

    /**
     * Who can I hurt? I can never hurt members or allies. I can always hurt enemies. I can hurt neutrals as long as
     * they are outside their own territory.
     */
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent sub = (EntityDamageByEntityEvent) event;
            if (!this.canDamagerHurtDamagee(sub, true)) {
                event.setCancelled(true);
            }
            // event is not cancelled by factions

            Entity damagee = sub.getEntity();
            Entity damager = sub.getDamager();

            if (damagee != null && damagee instanceof Player) {
                cancelFStuckTeleport((Player) damagee);
            }
            if (damager instanceof Player) {
                cancelFStuckTeleport((Player) damager);
            }
        } else if (Conf.safeZonePreventAllDamageToPlayers && isPlayerInSafeZone(event.getEntity())) {
            // Players can not take any damage in a Safe Zone
            event.setCancelled(true);
        }

        // entity took generic damage?
        Entity entity = event.getEntity();
        if (entity instanceof Player) {
            Player player = (Player) entity;
            FPlayer me = FPlayers.getInstance().getByPlayer(player);
            cancelFStuckTeleport(player);
            if (me.isWarmingUp()) {
                me.clearWarmup();
                me.msg(TL.WARMUPS_CANCELLED);
            }
        }
    }

    public void cancelFStuckTeleport(Player player) {
        if (player == null) {
            return;
        }
        UUID uuid = player.getUniqueId();
        if (P.p.getStuckMap().containsKey(uuid)) {
            FPlayers.getInstance().getByPlayer(player).msg(TL.COMMAND_STUCK_CANCELLED);
            P.p.getStuckMap().remove(uuid);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityExplode(EntityExplodeEvent event) {
        Location loc = event.getLocation();
        Entity boomer = event.getEntity();
        Faction faction = Board.getInstance().getFactionAt(new FLocation(loc));

        if (faction.noExplosionsInTerritory() || (faction.isPeaceful() && Conf.peacefulTerritoryDisableBoom)) {
            // faction is peaceful and has explosions set to disabled
            event.setCancelled(true);
            return;
        }

        boolean online = faction.hasPlayersOnline();

        //TODO: :(
        if (boomer instanceof Creeper && ((faction.isWilderness() && Conf.wildernessBlockCreepers && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) ||
                                                  (faction.isNormal() && (online ? Conf.territoryBlockCreepers : Conf.territoryBlockCreepersWhenOffline)) ||
                                                  (faction.isWarZone() && Conf.warZoneBlockCreepers) ||
                                                  faction.isSafeZone())) {
            // creeper which needs prevention
            event.setCancelled(true);
        } else if (
            // it's a bit crude just using fireball protection for Wither boss too, but I'd rather not add in a whole new set of xxxBlockWitherExplosion or whatever
                       (boomer instanceof Fireball || boomer instanceof WitherSkull || boomer instanceof Wither) && ((faction.isWilderness() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) ||
                                                                                                                             (faction.isNormal() && (online ? Conf.territoryBlockFireballs : Conf.territoryBlockFireballsWhenOffline)) ||
                                                                                                                             (faction.isWarZone() && Conf.warZoneBlockFireballs) ||
                                                                                                                             faction.isSafeZone())) {
            // ghast fireball which needs prevention
            event.setCancelled(true);
        } else if ((boomer instanceof TNTPrimed || boomer instanceof ExplosiveMinecart) && ((faction.isWilderness() && Conf.wildernessBlockTNT && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) ||
                                                                                                    (faction.isNormal() && (online ? Conf.territoryBlockTNT : Conf.territoryBlockTNTWhenOffline)) ||
                                                                                                    (faction.isWarZone() && Conf.warZoneBlockTNT) ||
                                                                                                    (faction.isSafeZone() && Conf.safeZoneBlockTNT))) {
            // TNT which needs prevention
            event.setCancelled(true);
        } else if ((boomer instanceof TNTPrimed || boomer instanceof ExplosiveMinecart) && Conf.handleExploitTNTWaterlog) {
            // TNT in water/lava doesn't normally destroy any surrounding blocks, which is usually desired behavior, but...
            // this change below provides workaround for waterwalling providing perfect protection,
            // and makes cheap (non-obsidian) TNT cannons require minor maintenance between shots
            Block center = loc.getBlock();
            if (center.isLiquid()) {
                // a single surrounding block in all 6 directions is broken if the material is weak enough
                List<Block> targets = new ArrayList<Block>();
                targets.add(center.getRelative(0, 0, 1));
                targets.add(center.getRelative(0, 0, -1));
                targets.add(center.getRelative(0, 1, 0));
                targets.add(center.getRelative(0, -1, 0));
                targets.add(center.getRelative(1, 0, 0));
                targets.add(center.getRelative(-1, 0, 0));
                for (Block target : targets) {
                    int id = target.getTypeId();
                    // ignore air, bedrock, water, lava, obsidian, enchanting table, etc.... too bad we can't get a blast resistance value through Bukkit yet
                    if (id != 0 && (id < 7 || id > 11) && id != 49 && id != 90 && id != 116 && id != 119 && id != 120 && id != 130) {
                        target.breakNaturally();
                    }
                }
            }
        }
    }

    // mainly for flaming arrows; don't want allies or people in safe zones to be ignited even after damage event is cancelled
    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityCombustByEntity(EntityCombustByEntityEvent event) {
        EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent(event.getCombuster(), event.getEntity(), EntityDamageEvent.DamageCause.FIRE, 0d);
        if (!this.canDamagerHurtDamagee(sub, false)) {
            event.setCancelled(true);
        }
        sub = null;
    }

    private static final Set<PotionEffectType> badPotionEffects = new LinkedHashSet<PotionEffectType>(Arrays.asList(PotionEffectType.BLINDNESS, PotionEffectType.CONFUSION, PotionEffectType.HARM, PotionEffectType.HUNGER, PotionEffectType.POISON, PotionEffectType.SLOW, PotionEffectType.SLOW_DIGGING, PotionEffectType.WEAKNESS, PotionEffectType.WITHER));

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPotionSplashEvent(PotionSplashEvent event) {
        // see if the potion has a harmful effect
        boolean badjuju = false;
        for (PotionEffect effect : event.getPotion().getEffects()) {
            if (badPotionEffects.contains(effect.getType())) {
                badjuju = true;
                break;
            }
        }
        if (!badjuju) {
            return;
        }

        ProjectileSource thrower = event.getPotion().getShooter();
        if (!(thrower instanceof Entity)) {
            return;
        }

        if (thrower instanceof Player) {
            Player player = (Player) thrower;
            FPlayer fPlayer = FPlayers.getInstance().getByPlayer(player);
            if (badjuju && fPlayer.getFaction().isPeaceful()) {
                event.setCancelled(true);
                return;
            }
        }

        // scan through affected entities to make sure they're all valid targets
        Iterator<LivingEntity> iter = event.getAffectedEntities().iterator();
        while (iter.hasNext()) {
            LivingEntity target = iter.next();
            EntityDamageByEntityEvent sub = new EntityDamageByEntityEvent((Entity) thrower, target, EntityDamageEvent.DamageCause.CUSTOM, 0);
            if (!this.canDamagerHurtDamagee(sub, true)) {
                event.setIntensity(target, 0.0);  // affected entity list doesn't accept modification (so no iter.remove()), but this works
            }
            sub = null;
        }
    }

    public boolean isPlayerInSafeZone(Entity damagee) {
        if (!(damagee instanceof Player)) {
            return false;
        }
        if (Board.getInstance().getFactionAt(new FLocation(damagee.getLocation())).isSafeZone()) {
            return true;
        }
        return false;
    }

    public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub) {
        return canDamagerHurtDamagee(sub, true);
    }

    public boolean canDamagerHurtDamagee(EntityDamageByEntityEvent sub, boolean notify) {
        Entity damager = sub.getDamager();
        Entity damagee = sub.getEntity();

        if (!(damagee instanceof Player)) {
            return true;
        }

        FPlayer defender = FPlayers.getInstance().getByPlayer((Player) damagee);

        if (defender == null || defender.getPlayer() == null) {
            return true;
        }

        Location defenderLoc = defender.getPlayer().getLocation();
        Faction defLocFaction = Board.getInstance().getFactionAt(new FLocation(defenderLoc));

        // for damage caused by projectiles, getDamager() returns the projectile... what we need to know is the source
        if (damager instanceof Projectile) {
            Projectile projectile = (Projectile) damager;

            if (!(projectile.getShooter() instanceof Entity)) {
                return true;
            }

            damager = (Entity) projectile.getShooter();
        }

        if (damager == damagee)  // ender pearl usage and other self-inflicted damage
        {
            return true;
        }

        // Players can not take attack damage in a SafeZone, or possibly peaceful territory
        if (defLocFaction.noPvPInTerritory()) {
            if (damager instanceof Player) {
                if (notify) {
                    FPlayer attacker = FPlayers.getInstance().getByPlayer((Player) damager);
                    attacker.msg(TL.PLAYER_CANTHURT, (defLocFaction.isSafeZone() ? TL.REGION_SAFEZONE.toString() : TL.REGION_PEACEFUL.toString()));
                }
                return false;
            }
            return !defLocFaction.noMonstersInTerritory();
        }

        if (!(damager instanceof Player)) {
            return true;
        }

        FPlayer attacker = FPlayers.getInstance().getByPlayer((Player) damager);

        if (attacker == null || attacker.getPlayer() == null) {
            return true;
        }

        if (Conf.playersWhoBypassAllProtection.contains(attacker.getName())) {
            return true;
        }

        if (attacker.hasLoginPvpDisabled()) {
            if (notify) {
                attacker.msg(TL.PLAYER_PVP_LOGIN, Conf.noPVPDamageToOthersForXSecondsAfterLogin);
            }
            return false;
        }

        Faction locFaction = Board.getInstance().getFactionAt(new FLocation(attacker));

        // so we know from above that the defender isn't in a safezone... what about the attacker, sneaky dog that he might be?
        if (locFaction.noPvPInTerritory()) {
            if (notify) {
                attacker.msg(TL.PLAYER_CANTHURT, (locFaction.isSafeZone() ? TL.REGION_SAFEZONE.toString() : TL.REGION_PEACEFUL.toString()));
            }
            return false;
        }

        if (locFaction.isWarZone() && Conf.warZoneFriendlyFire) {
            return true;
        }

        if (Conf.worldsIgnorePvP.contains(defenderLoc.getWorld().getName())) {
            return true;
        }

        Faction defendFaction = defender.getFaction();
        Faction attackFaction = attacker.getFaction();

        if (attackFaction.isWilderness() && Conf.disablePVPForFactionlessPlayers) {
            if (notify) {
                attacker.msg(TL.PLAYER_PVP_REQUIREFACTION);
            }
            return false;
        } else if (defendFaction.isWilderness()) {
            if (defLocFaction == attackFaction && Conf.enablePVPAgainstFactionlessInAttackersLand) {
                // Allow PVP vs. Factionless in attacker's faction territory
                return true;
            } else if (Conf.disablePVPForFactionlessPlayers) {
                if (notify) {
                    attacker.msg(TL.PLAYER_PVP_FACTIONLESS);
                }
                return false;
            }
        }

        if (defendFaction.isPeaceful()) {
            if (notify) {
                attacker.msg(TL.PLAYER_PVP_PEACEFUL);
            }
            return false;
        } else if (attackFaction.isPeaceful()) {
            if (notify) {
                attacker.msg(TL.PLAYER_PVP_PEACEFUL);
            }
            return false;
        }

        Relation relation = defendFaction.getRelationTo(attackFaction);

        // You can not hurt neutral factions
        if (Conf.disablePVPBetweenNeutralFactions && relation.isNeutral()) {
            if (notify) {
                attacker.msg(TL.PLAYER_PVP_NEUTRAL);
            }
            return false;
        }

        // Players without faction may be hurt anywhere
        if (!defender.hasFaction()) {
            return true;
        }

        // You can never hurt faction members or allies
        if (relation.isMember() || relation.isAlly()) {
            if (notify) {
                attacker.msg(TL.PLAYER_PVP_CANTHURT, defender.describeTo(attacker));
            }
            return false;
        }

        boolean ownTerritory = defender.isInOwnTerritory();

        // You can not hurt neutrals in their own territory.
        if (ownTerritory && relation.isNeutral()) {
            if (notify) {
                attacker.msg(TL.PLAYER_PVP_NEUTRALFAIL, defender.describeTo(attacker));
                defender.msg(TL.PLAYER_PVP_TRIED, attacker.describeTo(defender, true));
            }
            return false;
        }

        // Damage will be dealt. However check if the damage should be reduced.
        /*
        if (damage > 0.0 && ownTerritory && Conf.territoryShieldFactor > 0) {
            double newDamage = Math.ceil(damage * (1D - Conf.territoryShieldFactor));
            sub.setDamage(newDamage);

            // Send message
            if (notify) {
                String perc = MessageFormat.format("{0,number,#%}", (Conf.territoryShieldFactor)); // TODO does this display correctly??
                defender.msg("<i>Enemy damage reduced by <rose>%s<i>.", perc);
            }
        } */

        return true;
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getLocation() == null) {
            return;
        }

        if (Conf.safeZoneNerfedCreatureTypes.contains(event.getEntityType()) && Board.getInstance().getFactionAt(new FLocation(event.getLocation())).noMonstersInTerritory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityTarget(EntityTargetEvent event) {
        // if there is a target
        Entity target = event.getTarget();
        if (target == null) {
            return;
        }

        // We are interested in blocking targeting for certain mobs:
        if (!Conf.safeZoneNerfedCreatureTypes.contains(MiscUtil.creatureTypeFromEntity(event.getEntity()))) {
            return;
        }

        // in case the target is in a safe zone.
        if (Board.getInstance().getFactionAt(new FLocation(target.getLocation())).noMonstersInTerritory()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPaintingBreak(HangingBreakEvent event) {
        if (event.getCause() == RemoveCause.EXPLOSION) {
            Location loc = event.getEntity().getLocation();
            Faction faction = Board.getInstance().getFactionAt(new FLocation(loc));
            if (faction.noExplosionsInTerritory()) {
                // faction is peaceful and has explosions set to disabled
                event.setCancelled(true);
                return;
            }

            boolean online = faction.hasPlayersOnline();

            if ((faction.isWilderness() && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName()) && (Conf.wildernessBlockCreepers || Conf.wildernessBlockFireballs || Conf.wildernessBlockTNT)) ||
                        (faction.isNormal() && (online ? (Conf.territoryBlockCreepers || Conf.territoryBlockFireballs || Conf.territoryBlockTNT) : (Conf.territoryBlockCreepersWhenOffline || Conf.territoryBlockFireballsWhenOffline || Conf.territoryBlockTNTWhenOffline))) ||
                        (faction.isWarZone() && (Conf.warZoneBlockCreepers || Conf.warZoneBlockFireballs || Conf.warZoneBlockTNT)) ||
                        faction.isSafeZone()) {
                // explosion which needs prevention
                event.setCancelled(true);
            }
        }

        if (!(event instanceof HangingBreakByEntityEvent)) {
            return;
        }

        Entity breaker = ((HangingBreakByEntityEvent) event).getRemover();
        if (!(breaker instanceof Player)) {
            return;
        }

        if (!FactionsBlockListener.playerCanBuildDestroyBlock((Player) breaker, event.getEntity().getLocation(), "remove paintings", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onPaintingPlace(HangingPlaceEvent event) {
        if (!FactionsBlockListener.playerCanBuildDestroyBlock(event.getPlayer(), event.getBlock().getLocation(), "place paintings", false)) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        Entity entity = event.getEntity();

        // for now, only interested in Enderman and Wither boss tomfoolery
        if (!(entity instanceof Enderman) && !(entity instanceof Wither)) {
            return;
        }

        Location loc = event.getBlock().getLocation();

        if (entity instanceof Enderman) {
            if (stopEndermanBlockManipulation(loc)) {
                event.setCancelled(true);
            }
        } else if (entity instanceof Wither) {
            Faction faction = Board.getInstance().getFactionAt(new FLocation(loc));
            // it's a bit crude just using fireball protection, but I'd rather not add in a whole new set of xxxBlockWitherExplosion or whatever
            if ((faction.isWilderness() && Conf.wildernessBlockFireballs && !Conf.worldsNoWildernessProtection.contains(loc.getWorld().getName())) ||
                        (faction.isNormal() && (faction.hasPlayersOnline() ? Conf.territoryBlockFireballs : Conf.territoryBlockFireballsWhenOffline)) ||
                        (faction.isWarZone() && Conf.warZoneBlockFireballs) ||
                        faction.isSafeZone()) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onTravel(PlayerPortalEvent event) {
        if (!P.p.getConfig().getBoolean("portals.limit", false)) {
            return; // Don't do anything if they don't want us to.
        }

        TravelAgent agent = event.getPortalTravelAgent();

        // If they aren't able to find a portal, it'll try to create one.
        if (event.useTravelAgent() && agent.getCanCreatePortal() && agent.findPortal(event.getTo()) == null) {
            FLocation loc = new FLocation(event.getTo());
            Faction faction = Board.getInstance().getFactionAt(loc);
            if (faction.isWilderness()) {
                return; // We don't care about wilderness.
            } else if (!faction.isNormal() && !event.getPlayer().isOp()) {
                // Don't let non ops make portals in safezone or warzone.
                event.setCancelled(true);
                return;
            }

            FPlayer fp = FPlayers.getInstance().getByPlayer(event.getPlayer());
            String mininumRelation = P.p.getConfig().getString("portals.minimum-relation", "MEMBER"); // Defaults to Neutral if typed wrong.
            if (!fp.getFaction().getRelationTo(faction).isAtLeast(Relation.fromString(mininumRelation))) {
                event.setCancelled(true);
            }
        }
    }

    private boolean stopEndermanBlockManipulation(Location loc) {
        if (loc == null) {
            return false;
        }
        // quick check to see if all Enderman deny options are enabled; if so, no need to check location
        if (Conf.wildernessDenyEndermanBlocks &&
                    Conf.territoryDenyEndermanBlocks &&
                    Conf.territoryDenyEndermanBlocksWhenOffline &&
                    Conf.safeZoneDenyEndermanBlocks &&
                    Conf.warZoneDenyEndermanBlocks) {
            return true;
        }

        FLocation fLoc = new FLocation(loc);
        Faction claimFaction = Board.getInstance().getFactionAt(fLoc);

        if (claimFaction.isWilderness()) {
            return Conf.wildernessDenyEndermanBlocks;
        } else if (claimFaction.isNormal()) {
            return claimFaction.hasPlayersOnline() ? Conf.territoryDenyEndermanBlocks : Conf.territoryDenyEndermanBlocksWhenOffline;
        } else if (claimFaction.isSafeZone()) {
            return Conf.safeZoneDenyEndermanBlocks;
        } else if (claimFaction.isWarZone()) {
            return Conf.warZoneDenyEndermanBlocks;
        }

        return false;
    }
}
