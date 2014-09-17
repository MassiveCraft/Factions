package com.massivecraft.factions;

import java.util.LinkedHashSet;
import java.util.Set;

import org.bukkit.World;

public class WorldExceptionSet
{
	// -------------------------------------------- //
	// FIELDS
	// -------------------------------------------- //
	
	public boolean standard = true;
	
	public Set<String> exceptions = new LinkedHashSet<String>();
	
	// -------------------------------------------- //
	// CONTAINS
	// -------------------------------------------- //
	
	public boolean contains(String world)
	{
		if (this.exceptions.contains(world)) return !this.standard;
		return this.standard;
	}
	
	public boolean contains(World world)
	{
		return this.contains(world.getName());
	}

}
