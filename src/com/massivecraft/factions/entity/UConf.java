package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.command.CommandSender;
import org.bukkit.event.EventPriority;

import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Rel;
import com.massivecraft.factions.event.FactionsEventChunkChangeType;
import com.massivecraft.mcore.store.Entity;
import com.massivecraft.mcore.store.SenderEntity;
import com.massivecraft.mcore.util.MUtil;
import com.massivecraft.mcore.util.Txt;

public class UConf extends Entity<UConf>
{
	// -------------------------------------------- //
	// META
	// -------------------------------------------- //
	
	public static UConf get(Object oid)
	{
		return UConfColls.get().get2(oid);
	}
	
	// -------------------------------------------- //
	// UNIVERSE ENABLE SWITCH
	// -------------------------------------------- //
	
	public boolean enabled = true;
	
	public static boolean isDisabled(Object universe)
	{
		return isDisabled(universe, null);
	}
	
	public static String getDisabledMessage(Object universe)
	{
		UConf uconf = UConf.get(universe);
		return Txt.parse("<i>Factions are disabled in the <h>%s <i>universe.", uconf.getUniverse());
	}
	
	public static boolean isDisabled(Object universe, Object inform)
	{
		UConf uconf = UConf.get(universe);
		if (uconf.enabled) return false;
		
		if (inform instanceof CommandSender)
		{
			((CommandSender)inform).sendMessage(getDisabledMessage(universe));
		}
		else if (inform instanceof SenderEntity)
		{
			((SenderEntity<?>)inform).sendMessage(getDisabledMessage(universe));
		}
		
		return true;
	}
	
	// -------------------------------------------- //
	// SPECIAL FACTION IDS
	// -------------------------------------------- //
	
	public String factionIdNone = UUID.randomUUID().toString();
	public String factionIdSafezone = UUID.randomUUID().toString();
	public String factionIdWarzone = UUID.randomUUID().toString();
	
	// -------------------------------------------- //
	// DEFAULTS
	// -------------------------------------------- //
	
	public String defaultPlayerFactionId = this.factionIdNone;
	public Rel defaultPlayerRole = Rel.RECRUIT;
	public double defaultPlayerPower = 0.0;
	
	public boolean defaultFactionOpen = false;
	public Map<FFlag, Boolean> defaultFactionFlags = FFlag.getDefaultDefaults();
	public Map<FPerm, Set<Rel>> defaultFactionPerms = FPerm.getDefaultDefaults();

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
	public EventPriority homesTeleportToOnDeathPriority = EventPriority.NORMAL;
	
	// -------------------------------------------- //
	// ASSORTED
	// -------------------------------------------- //
	
	public boolean permanentFactionsDisableLeaderPromotion = false;
	public int actionDeniedPainAmount = 2;
	public boolean disablePVPForFactionlessPlayers = false;
	public boolean enablePVPAgainstFactionlessInAttackersLand = false;
	public double territoryShieldFactor = 0.3;
	
	// -------------------------------------------- //
	// DENY COMMANDS
	// -------------------------------------------- //
	
	// commands which will be prevented if the player is a member of a permanent faction
	public List<String> denyCommandsPermanentFactionMember = new ArrayList<String>();

	// commands which will be prevented when in claimed territory of another faction
	public Map<Rel, List<String>> denyCommandsTerritoryRelation = MUtil.map(
		Rel.ENEMY, MUtil.list(
			"home",
			"sethome",
			"tpahere",
			"tpaccept",
			"tpa",
			"warp",
			"warps",
			"spawn",
			"wtp",
			"uspawn",
			"utp",
			"mspawn",
			"mtp",
			"fspawn",
			"ftp",
			"jspawn",
			"jtp"
		),
		Rel.NEUTRAL, new ArrayList<String>(),
		Rel.TRUCE, new ArrayList<String>(),
		Rel.ALLY, new ArrayList<String>(),
		Rel.MEMBER, new ArrayList<String>()
	);
	
	// -------------------------------------------- //
	// INTEGRATION: LWC
	// -------------------------------------------- //
	
	public Map<FactionsEventChunkChangeType, Boolean> lwcRemoveOnChange = MUtil.map(
		FactionsEventChunkChangeType.BUY, false,
		FactionsEventChunkChangeType.SELL, false,
		FactionsEventChunkChangeType.CONQUER, false,
		FactionsEventChunkChangeType.PILLAGE, false
	);
	
	// -------------------------------------------- //
	// INTEGRATION: ECONOMY
	// -------------------------------------------- //
	
	public boolean econEnabled = false;
	
	// TODO: Rename to include unit.
	public double econLandReward = 0.00;
	
	public String econUniverseAccount = "";
	
	public Map<FactionsEventChunkChangeType, Double> econChunkCost = MUtil.map(
		FactionsEventChunkChangeType.BUY, 30.0,
		FactionsEventChunkChangeType.SELL, -20.0,
		FactionsEventChunkChangeType.CONQUER, -10.0,
		FactionsEventChunkChangeType.PILLAGE, -10.0
	);
	
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
	
	public Map<Rel, Double> econRelCost = MUtil.map(
		Rel.ENEMY, 0.0,
		Rel.ALLY, 0.0,
		Rel.TRUCE, 0.0,
		Rel.NEUTRAL, 0.0
	);
	
	//Faction banks, to pay for land claiming and other costs instead of individuals paying for them
	public boolean bankEnabled = true;
	//public static boolean bankMembersCanWithdraw = false; //Have to be at least moderator to withdraw or pay money to another faction
	public boolean bankFactionPaysCosts = true; //The faction pays for faction command costs, such as sethome
	public boolean bankFactionPaysLandCosts = true; //The faction pays for land claiming costs.

}
