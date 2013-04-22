package com.massivecraft.factions.entity;

import java.util.Map;
import java.util.Set;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Rel;
import com.massivecraft.mcore.store.Entity;

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
	// CORE
	// -------------------------------------------- //
	
	public Map<FFlag, Boolean> factionFlagDefaults = FFlag.getDefaultDefaults();
	public Map<FPerm, Set<Rel>> factionPermDefaults = FPerm.getDefaultDefaults();
	
	public String playerDefaultFactionId = Const.FACTIONID_NONE;
	public Rel playerDefaultRole = Rel.RECRUIT;
	
	public boolean canLeaveWithNegativePower = true;
	
	public int factionTagLengthMin = 3;
	public int factionTagLengthMax = 10;
	public boolean factionTagForceUpperCase = false;
	
	public boolean newFactionsDefaultOpen = false;

	public int factionMemberLimit = 0;
	
	// -------------------------------------------- //
	// POWER
	// -------------------------------------------- //
	
	public double powerMax = 10.0;
	public double powerMin = -10.0;
	public double powerStarting = 10.0; // New players start out with this power level
	
	public double powerPerDeath = -4.0; // A death makes you lose 4 power
	
	public double powerPerHourOnline = 10.0;
	public double powerPerHourOffline = 0.0;
	
	// players will no longer lose power from being offline once their power drops to this amount or less
	public double powerLimitGainOnline = 10.0;
	public double powerLimitGainOffline = 0.0;
	public double powerLimitLossOnline = -10.0;
	public double powerLimitLossOffline = 0.0;
	
	public boolean scaleNegativePower = false; // Power regeneration rate increase as power decreases
	public double scaleNegativeDivisor = 40.0; // Divisor for inverse power regeneration curve
	
	public double powerFactionMax = 0.0;  // if greater than 0, the cap on how much power a faction can have (additional power from players beyond that will act as a "buffer" of sorts)
	
}
