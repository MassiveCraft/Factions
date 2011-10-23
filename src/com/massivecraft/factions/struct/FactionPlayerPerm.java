package com.massivecraft.factions.struct;

/**
 * Permissions that you (a player) may or may not have in the territory of a certain faction.
 * 
 * You need a certain rel to be able
 */
public enum FactionPlayerPerm
{
	BUILD, // This player can build in the faction
	PAINBUILD, // This player can build in the faction BUT will take damage each time. This is overridden by BUILD if player has both
	DOOR,
	WORKBENCH,
	CONTAINER,
	BUTTON,
	LEVER,
}
