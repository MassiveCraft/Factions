package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.event.EventFactionsChunkChangeType;
import com.massivecraft.massivecore.command.type.enumeration.TypeEnum;

public class TypeFactionChunkChangeType extends TypeEnum<EventFactionsChunkChangeType>
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private static TypeFactionChunkChangeType i = new TypeFactionChunkChangeType();
	public static TypeFactionChunkChangeType get() { return i; }
	public TypeFactionChunkChangeType()
	{
		super(EventFactionsChunkChangeType.class);
	}
	
}
