package com.massivecraft.factions.struct;

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
	PERMANENT,
	PEACEFUL, // This faction is friends with everyone
	INFPOWER, // This faction has infinite power: TODO: Add faction has enough method. Replace the permanentpower level 
	
	// (Faction) Territory flags
	POWERLOSS, // Regardless of death-reason players loose power on death IF powerloss is true in this territory
	PVP,
	FRIENDLYFIRE, // Can members/allies/friends damage eachother in this territory?
	MONSTERS,
	EXPLOSIONS,
	FIRESPREAD,
	LIGHTNING,
	;
	
	/**
	 * The state for newly created factions.
	 */
	public boolean getDefault()
	{
		// Use config file for this later.
		return true;
	}
	
	/**
	 * Is this flag changeable by the faction leaders or not?
	 * The normal faction members can never change these flags.
	 * Note that server operators and admin bypassers can change all flags.
	 */
	public boolean isChangeable()
	{
		// TODO: Use config file
		return true;
	}
}
