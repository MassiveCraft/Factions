package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.command.type.enumeration.TypeEnum;

import java.util.Set;

public class TypeRel extends TypeEnum<Rel>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeRel i = new TypeRel();
	public static TypeRel get() { return i; }
	public TypeRel() { super(Rel.class); }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //
	
	@Override
	public String getName()
	{
		return "role";
	}
	
	@Override
	public String getNameInner(Rel value)
	{
		return value.getName();
	}
	
	@Override
	public Set<String> getNamesInner(Rel value)
	{
		return value.getNames();
	}
	
}
