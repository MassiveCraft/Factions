package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.Rel;

public class TypeRelation extends TypeRel
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //

	private static TypeRelation i = new TypeRelation();
	public static TypeRelation get() { return i; }
	public TypeRelation() { this.setAll(Rel.NEUTRAL, Rel.TRUCE, Rel.ALLY, Rel.ENEMY); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getName()
	{
		return "relation";
	}
	
}
