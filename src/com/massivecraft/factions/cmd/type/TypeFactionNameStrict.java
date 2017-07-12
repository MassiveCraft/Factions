package com.massivecraft.factions.cmd.type;

public class TypeFactionNameStrict extends TypeFactionNameAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeFactionNameStrict i = new TypeFactionNameStrict();
	public static TypeFactionNameStrict get() {return i; }
	public TypeFactionNameStrict()
	{
		super(true);
	}
	
}
