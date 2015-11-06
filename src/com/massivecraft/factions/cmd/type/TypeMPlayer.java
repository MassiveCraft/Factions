package com.massivecraft.factions.cmd.type;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.command.type.Type;

public class TypeMPlayer
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	public static Type<MPlayer> get()
	{
		return MPlayerColl.get().getTypeEntity();
	}
	
}
