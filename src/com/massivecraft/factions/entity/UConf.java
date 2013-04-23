package com.massivecraft.factions.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.massivecraft.factions.Const;
import com.massivecraft.factions.FFlag;
import com.massivecraft.factions.FPerm;
import com.massivecraft.factions.Rel;
import com.massivecraft.mcore.store.Entity;
import com.massivecraft.mcore.util.MUtil;

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
	
	// TODO: Group default values together?
	
	public double powerPlayerDefault = 0.0;
	
	public double powerFactionMax = 1000.0;
	
	// -------------------------------------------- //
	// DENY COMMANDS
	// -------------------------------------------- //
	
	// commands which will be prevented if the player is a member of a permanent faction
	public List<String> denyCommandsPermanentFactionMember = new ArrayList<String>();

	// commands which will be prevented when in claimed territory of another faction
	public List<String> denyCommandsTerritoryNeutral = new ArrayList<String>();
	public List<String> denyCommandsTerritoryEnemy = MUtil.list(
		"home",
		"sethome",
		"spawn",
		"tpahere",
		"tpaccept",
		"tpa",
		"warp"
	);
	
}
