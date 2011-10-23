package com.massivecraft.factions.struct;

/**
 * Permissions that you (a player) may or may not have in the territory of a certain faction.
 * Each faction have many Rel's assigned to each one of these Perms. 
 */
public enum FactionPerm
{
	BUILD, // This player can build in the faction
	PAINBUILD, // This player can build in the faction BUT will take damage each time. This is overridden by BUILD if player has both
	DOOR,
	WORKBENCH,
	CONTAINER,
	BUTTON,
	LEVER,
}
