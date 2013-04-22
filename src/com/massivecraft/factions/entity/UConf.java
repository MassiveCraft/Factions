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
	
}
