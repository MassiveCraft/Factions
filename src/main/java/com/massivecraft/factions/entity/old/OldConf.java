package com.massivecraft.factions.entity.old;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.event.EventPriority;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.massivecore.store.Entity;

public class OldConf extends Entity<OldConf>
{
	// -------------------------------------------- //
	// UNIVERSE ENABLE SWITCH
	// -------------------------------------------- //
	
	public boolean enabled = true;
	
	// -------------------------------------------- //
	// SPECIAL FACTION IDS
	// -------------------------------------------- //
	
	public String factionIdNone = null;
	public String factionIdSafezone = null;
	public String factionIdWarzone = null;
	
	// -------------------------------------------- //
	// DEFAULTS
	// -------------------------------------------- //
	
	public String defaultPlayerFactionId = null;
	public Rel defaultPlayerRole = null;
	public double defaultPlayerPower = 0.0;
	
	public boolean defaultFactionOpen = false;
	public Map<FFlag, Boolean> defaultFactionFlags = null;
	public Map<FPerm, Set<Rel>> defaultFactionPerms = null;

	// -------------------------------------------- //
	// MESSAGES
	// -------------------------------------------- //
	
	public boolean broadcastNameChange = false;
	
	// -------------------------------------------- //
	// POWER
	// -------------------------------------------- //
	
	public double powerMax = 10.0;
	public double powerMin = 0.0;
	public double powerPerHour = 2.0;
	public double powerPerDeath = -2.0;
	
	public boolean canLeaveWithNegativePower = true;
	
	// -------------------------------------------- //
	// CORE
	// -------------------------------------------- //

	public int factionMemberLimit = 0;
	public double factionPowerMax = 0.0;
	
	public int factionNameLengthMin = 3;
	public int factionNameLengthMax = 16;
	public boolean factionNameForceUpperCase = false;
	
	// -------------------------------------------- //
	// CLAIMS
	// -------------------------------------------- //
	
	public boolean claimsMustBeConnected = true;
	public boolean claimingFromOthersAllowed = true;
	public boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = false;
	public int claimsRequireMinFactionMembers = 1;
	public int claimedLandsMax = 0;
	
	// -------------------------------------------- //
	// HOMES
	// -------------------------------------------- //
	
	public boolean homesEnabled = true;
	public boolean homesMustBeInClaimedTerritory = true;
	public boolean homesTeleportCommandEnabled = true;
	public boolean homesTeleportAllowedFromEnemyTerritory = true;
	public boolean homesTeleportAllowedFromDifferentWorld = true;
	public double homesTeleportAllowedEnemyDistance = 32.0;
	public boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
	
	public boolean homesTeleportToOnDeathActive = false;
	public EventPriority homesTeleportToOnDeathPriority = null;
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	public boolean permanentFactionsDisableLeaderPromotion = false;
	public double actionDeniedPainAmount = 2.0D;
	public boolean disablePVPForFactionlessPlayers = false;
	public boolean enablePVPAgainstFactionlessInAttackersLand = false;
	public double territoryShieldFactor = 0.3D;
	
	// -------------------------------------------- //
	// DENY COMMANDS
	// -------------------------------------------- //
	
	// commands which will be prevented if the player is a member of a permanent faction
	public List<String> denyCommandsPermanentFactionMember = null;

	// commands which will be prevented when in claimed territory of another faction
	public Map<Rel, List<String>> denyCommandsTerritoryRelation = null;
	
	// -------------------------------------------- //
	// INTEGRATION: LWC
	// -------------------------------------------- //
	
	public Map<EventFactionsChunkChangeType, Boolean> lwcRemoveOnChange = null;
	
	// -------------------------------------------- //
	// INTEGRATION: ECONOMY
	// -------------------------------------------- //
	
	public boolean econEnabled = false;
	
	// TODO: Rename to include unit.
	public double econLandReward = 0.00;
	
	public String econUniverseAccount = null;
	
	public Map<EventFactionsChunkChangeType, Double> econChunkCost = null;
	
	public double econCostCreate = 200.0;
	public double econCostSethome = 0.0;
	public double econCostJoin = 0.0;
	public double econCostLeave = 0.0;
	public double econCostKick = 0.0;
	public double econCostInvite = 0.0;
	public double econCostDeinvite = 0.0;
	public double econCostHome = 0.0;
	public double econCostName = 0.0;
	public double econCostDescription = 0.0;
	public double econCostTitle = 0.0;
	public double econCostOpen = 0.0;
	
	public Map<Rel, Double> econRelCost = null;
	
	//Faction banks, to pay for land claiming and other costs instead of individuals paying for them
	public boolean bankEnabled = true;
	//public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
	public boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
	public boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.
}
