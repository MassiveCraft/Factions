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
	
	public boolean canLeaveWithNegativePower = true;
	
	public int factionMemberLimit = 0;
	public double factionPowerMax = 1000.0;
	
	public int factionTagLengthMin = 3;
	public int factionTagLengthMax = 10;
	public boolean factionTagForceUpperCase = false;
	
	// -------------------------------------------- //
	// DEFAULTS
	// -------------------------------------------- //
	
	// TODO: should I add a nofaction id here?
	// And perhaps for safezone and warzone as well.
	
	public String defaultPlayerFactionId = Const.FACTIONID_NONE;
	public double defaultPlayerPower = 0.0;
	public Rel defaultPlayerRole = Rel.RECRUIT;
	
	public boolean defaultFactionOpen = false;
	public Map<FFlag, Boolean> defaultFactionFlags = FFlag.getDefaultDefaults();
	public Map<FPerm, Set<Rel>> defaultFactionPerms = FPerm.getDefaultDefaults();
	
	// -------------------------------------------- //
	// DENY COMMANDS
	// -------------------------------------------- //
	
	// commands which will be prevented if the player is a member of a permanent faction
	public List<String> denyCommandsPermanentFactionMember = new ArrayList<String>();

	// commands which will be prevented when in claimed territory of another faction
	public Map<Rel, List<String>> denyCommandsTerritoryRelation = MUtil.map(
		Rel.ENEMY, MUtil.list("home", "sethome", "spawn", "tpahere", "tpaccept", "tpa", "warp"),
		Rel.NEUTRAL, new ArrayList<String>(),
		Rel.TRUCE, new ArrayList<String>(),
		Rel.ALLY, new ArrayList<String>(),
		Rel.MEMBER, new ArrayList<String>()
	);
	
}
