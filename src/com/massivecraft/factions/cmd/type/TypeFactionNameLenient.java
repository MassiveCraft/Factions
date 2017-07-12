package com.massivecraft.factions.cmd.type;

public class TypeFactionNameLenient extends TypeFactionNameAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeFactionNameLenient i = new TypeFactionNameLenient();
	public static TypeFactionNameLenient get() { return i; }
	public TypeFactionNameLenient()
	{
		super(false);
	}
	
}
