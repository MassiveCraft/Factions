package com.massivecraft.factions;

import java.util.*;

import com.massivecraft.mcore.SimpleConfig;
import com.massivecraft.mcore.util.MUtil;

public class ConfServer extends SimpleConfig
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static transient ConfServer i = new ConfServer();
	public static ConfServer get() { return i; }
	public ConfServer() { super(Factions.get()); }
	
	// -------------------------------------------- //
	// CORE
	// -------------------------------------------- //
	
	public static List<String> baseCommandAliases = MUtil.list("f");
	public static String dburi = "default";
	
	// -------------------------------------------- //
	// AUTO LEAVE
	// -------------------------------------------- //

	public static double autoLeaveAfterDaysOfInactivity = 10.0;
	public static double autoLeaveRoutineRunsEveryXMinutes = 5.0;
	public static boolean removePlayerDataWhenBanned = true;
	
	// -------------------------------------------- //
	// HOMES
	// -------------------------------------------- //
	
	public static boolean homesEnabled = true;
	public static boolean homesMustBeInClaimedTerritory = true;
	public static boolean homesTeleportCommandEnabled = true;
	public static boolean homesTeleportAllowedFromEnemyTerritory = true;
	public static boolean homesTeleportAllowedFromDifferentWorld = true;
	public static double homesTeleportAllowedEnemyDistance = 32.0;
	public static boolean homesTeleportIgnoreEnemiesIfInOwnTerritory = true;
	
	// -------------------------------------------- //
	// PVP
	// -------------------------------------------- //
	
	public static boolean disablePVPForFactionlessPlayers = false;
	public static boolean enablePVPAgainstFactionlessInAttackersLand = false;
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	public static boolean permanentFactionsDisableLeaderPromotion = false;
	
	// -------------------------------------------- //
	// CLAIMS
	// -------------------------------------------- //
	
	public static boolean claimsMustBeConnected = false;
	public static boolean claimingFromOthersAllowed = true;
	public static boolean claimsCanBeUnconnectedIfOwnedByOtherFaction = true;
	public static int claimsRequireMinFactionMembers = 1;
	public static int claimedLandsMax = 0;

	// if someone is doing a radius claim and the process fails to claim land this many times in a row, it will exit
	public static int radiusClaimFailureLimit = 9;

	//public static double considerFactionsReallyOfflineAfterXMinutes = 0.0;
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	public static int actionDeniedPainAmount = 2;
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	// TODO: Should this be based on a permission node lookup map?
	public static double territoryShieldFactor = 0.3;

	// for claimed areas where further faction-member ownership can be defined

	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	public static boolean pistonProtectionThroughDenyBuild = true;	

	// -------------------------------------------- //
	// INTEGRATION: WORLD GUARD
	// -------------------------------------------- //
	
	public static boolean worldGuardChecking = false;

	// -------------------------------------------- //
	// INTEGRATION: LWC
	// -------------------------------------------- //
	
	public static boolean onUnclaimResetLwcLocks = false;
	public static boolean onCaptureResetLwcLocks = false;
	
	// -------------------------------------------- //
	// INTEGRATION: ECONOMY
	// -------------------------------------------- //
	
	public static boolean econEnabled = false;
	public static String econUniverseAccount = "";
	
	public static double econCostClaimWilderness = 30.0;
	public static double econCostClaimFromFactionBonus = 30.0;
	public static double econClaimAdditionalMultiplier = 0.5;
	public static double econClaimRefundMultiplier = 0.7;
	public static double econClaimUnconnectedFee = 0.0;
	
	public static double econCostCreate = 100.0;
	public static double econCostSethome = 30.0;
	public static double econCostJoin = 0.0;
	public static double econCostLeave = 0.0;
	public static double econCostKick = 0.0;
	public static double econCostInvite = 0.0;
	public static double econCostDeinvite = 0.0;
	public static double econCostHome = 0.0;
	public static double econCostTag = 0.0;
	public static double econCostDescription = 0.0;
	public static double econCostTitle = 0.0;
	public static double econCostOpen = 0.0;
	public static double econCostAlly = 0.0;
	public static double econCostTruce = 0.0;
	public static double econCostNeutral = 0.0;
	public static double econCostEnemy = 0.0;

	public static int econLandRewardTaskRunsEveryXMinutes = 20;
	public static double econLandReward = 0.00;
	
	//Faction banks, to pay for land claiming and other costs instead of individuals paying for them
	public static boolean bankEnabled = true;
	//public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
	public static boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
	public static boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.
	
}

