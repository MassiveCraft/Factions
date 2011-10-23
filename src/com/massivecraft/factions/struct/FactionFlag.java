package com.massivecraft.factions.struct;

import com.massivecraft.factions.Conf;

/**
 * Flags that describe the nature of a faction and it's territory.
 * Can monsters spawn there? May fire spread etc? Is the faction permanent?
 * These flags have nothing to do with player-permission.
 * 
 * The flags are either true or false.
 */
public enum FactionFlag
{
	// Faction flags
	PERMANENT("permanent", "<i>A permanent faction will never be deleted.", false, false),
	PEACEFUL("peaceful", "<i>Allways in truce with other factions.", false, false),
	INFPOWER("infpower", "<i>This flag gives the faction infinite power.", false, false),
	// This faction has infinite power: TODO: Add faction has enough method. Replace the permanentpower level 
	
	// (Faction) Territory flags
	POWERLOSS("powerloss", "<i>Is power lost on death in this territory?", true, false),
	PVP("pvp", "<i>Can you PVP in territory?", true, false),
	FRIENDLYFIRE("friendlyfire", "<i>Can friends hurt eachother here?", false, false),
	MONSTERS("monsters", "<i>Can monsters spawn in this territory?", true, false),
	EXPLOSIONS("explosions", "<i>Can explosions occur in this territory?", true, false),
	FIRESPREAD("firespread", "<i>Can fire spread in territory?", true, false),
	LIGHTNING("lightning", "<i>Can lightning strike in this territory?", true, false),
	ENDERGRIEF("endergrief", "<i>Can endermen grief in this territory?", false, true),
	;
	
	private final String nicename;
	private final String desc;
	public final boolean defaultDefaultValue;
	public final boolean defaultDefaultChangeable;
	
	private FactionFlag(final String nicename, final String desc, final boolean defaultDefaultValue, final boolean defaultDefaultChangeable)
	{
		this.nicename = nicename;
		this.desc = desc;
		this.defaultDefaultValue = defaultDefaultValue;
		this.defaultDefaultChangeable = defaultDefaultChangeable;
	}
	
	public String getNicename()
	{
		return this.nicename;
	}
	
	public String getDescription()
	{
		return this.desc;
	}
	
	/**
	 * The state for newly created factions.
	 */
	public boolean getDefault()
	{
		Boolean ret = Conf.factionFlagDefaults.get(this);
		if (ret == null) return this.defaultDefaultValue;
		return ret; 
	}
	
	/**
	 * Is this flag changeable by the faction leaders or not?
	 * The normal faction members can never change these flags.
	 * Note that server operators and admin bypassers can change all flags.
	 */
	public boolean isChangeable()
	{
		Boolean ret = Conf.factionFlagIsChangeable.get(this);
		if (ret == null) return this.defaultDefaultChangeable;
		return ret; 
	}
	
	public static FactionFlag parse(String str)
	{
		str = str.toLowerCase();
		if (str.startsWith("per")) return PERMANENT;
		if (str.startsWith("pea")) return PEACEFUL;
		if (str.startsWith("i")) return INFPOWER;
		if (str.startsWith("pow")) return POWERLOSS;
		if (str.startsWith("pvp")) return PVP;
		if (str.startsWith("fr") || str.startsWith("ff")) return FRIENDLYFIRE;
		if (str.startsWith("m")) return MONSTERS;
		if (str.startsWith("e")) return EXPLOSIONS;
		if (str.startsWith("fi")) return FIRESPREAD;
		if (str.startsWith("l")) return LIGHTNING;
		return null;
	}
	
	public String getStateInfo(boolean value, boolean withDesc)
	{
		String ret = (value ? "<g>YES" : "<b>NOO") + "<h> " + this.getNicename();
		if (withDesc)
		{
			ret += " " + this.getDescription();
		}
		return ret;
	}
}
