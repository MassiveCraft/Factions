package com.massivecraft.factions.cmd.type;

import java.util.Set;

import com.massivecraft.factions.Rel;
import com.massivecraft.massivecore.command.type.enumeration.TypeEnum;

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
