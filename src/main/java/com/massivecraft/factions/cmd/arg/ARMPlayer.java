package com.massivecraft.factions.cmd.arg;

import com.massivecraft.factions.entity.MPlayer;
import com.massivecraft.factions.entity.MPlayerColl;
import com.massivecraft.massivecore.cmd.arg.ArgReader;

public class ARMPlayer
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	public static ArgReader<MPlayer> getAny()
	{
		return MPlayerColl.get().getAREntity();
	}
	
	public static ArgReader<MPlayer> getOnline()
	{
		return MPlayerColl.get().getAREntity(true);
	}
	
}
