package com.massivecraft.factions.cmd.arg;

import com.massivecraft.factions.entity.UPlayer;
import com.massivecraft.factions.entity.UPlayerColls;
import com.massivecraft.massivecore.cmd.arg.ArgReader;

public class ARUPlayer
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	public static ArgReader<UPlayer> getAny(Object o) { return UPlayerColls.get().get(o).getAREntity(); }
	
	public static ArgReader<UPlayer> getOnline(Object o) { return UPlayerColls.get().get(o).getAREntity(true); }
	
}
