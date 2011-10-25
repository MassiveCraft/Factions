package com.massivecraft.factions.struct;

import com.massivecraft.factions.Conf;

/**
 * Flags that describe the nature of a faction and it's territory.
 * Can monsters spawn there? May fire spread etc? Is the faction permanent?
 * These flags have nothing to do with player-permission.
 * 
 * The flags are either true or false.
 */
public enum FFlag
{
	// Faction flags
	PERMANENT("permanent", "<i>A permanent faction will never be deleted.", false),
	PEACEFUL("peaceful", "<i>Allways in truce with other factions.", false),
	INFPOWER("infpower", "<i>This flag gives the faction infinite power.", false),
	// This faction has infinite power: TODO: Add faction has enough method. Replace the permanentpower level 
	
	// (Faction) Territory flags
	// If a faction later could have many different territories this would probably be in another enum
	POWERLOSS("powerloss", "<i>Is power lost on death in this territory?", true),
	PVP("pvp", "<i>Can you PVP in territory?", true),
	FRIENDLYFIRE("friendlyfire", "<i>Can friends hurt eachother here?", false),
	MONSTERS("monsters", "<i>Can monsters spawn in this territory?", true),
	EXPLOSIONS("explosions", "<i>Can explosions occur in this territory?", true),
	FIRESPREAD("firespread", "<i>Can fire spread in territory?", true),
	//LIGHTNING("lightning", "<i>Can lightning strike in this territory?", true), Possible to add later.
	ENDERGRIEF("endergrief", "<i>Can endermen grief in this territory?", false),
	;
	
	private final String nicename;
	private final String desc;
	public final boolean defaultDefaultValue;
	
	private FFlag(final String nicename, final String desc, final boolean defaultDefaultValue)
	{
		this.nicename = nicename;
		this.desc = desc;
		this.defaultDefaultValue = defaultDefaultValue;
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
	
	public static FFlag parse(String str)
	{
		str = str.toLowerCase();
		if (str.startsWith("per")) return PERMANENT;
		if (str.startsWith("pea")) return PEACEFUL;
		if (str.startsWith("i")) return INFPOWER;
		if (str.startsWith("pow")) return POWERLOSS;
		if (str.startsWith("pvp")) return PVP;
		if (str.startsWith("fr") || str.startsWith("ff")) return FRIENDLYFIRE;
		if (str.startsWith("m")) return MONSTERS;
		if (str.startsWith("ex")) return EXPLOSIONS;
		if (str.startsWith("fi")) return FIRESPREAD;
		//if (str.startsWith("l")) return LIGHTNING;
		if (str.startsWith("en")) return ENDERGRIEF;		
		return null;
	}
	
	public String getStateInfo(boolean value, boolean withDesc)
	{
		String ret = (value ? "<g>YES" : "<b>NOO") + "<c> " + this.getNicename();
		if (withDesc)
		{
			ret += " " + this.getDescription();
		}
		return ret;
	}
}
