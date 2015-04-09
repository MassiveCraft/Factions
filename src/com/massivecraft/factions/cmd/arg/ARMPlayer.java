package com.massivecraft.factions.cmd.arg;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.cmd.arg.AR;

public class ARMPlayer
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	public static AR<MPlayer> getAny()
	{
		return MPlayerColl.get().getAREntity();
	}
	
	public static AR<MPlayer> getOnline()
	{
		return MPlayerColl.get().getAREntity(true);
	}
	
}
