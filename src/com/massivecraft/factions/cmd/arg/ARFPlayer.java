package com.massivecraft.factions.cmd.arg;

import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.FPlayerColl;
import com.massivecraft.mcore.cmd.arg.ARSenderEntity;
import com.massivecraft.mcore.cmd.arg.ArgReader;

public class ARFPlayer
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	private static ArgReader<FPlayer> fullAny = ARSenderEntity.getFullAny(FPlayerColl.get());
	public static ArgReader<FPlayer> getFullAny() { return fullAny; }
	
	private static ArgReader<FPlayer> startAny = ARSenderEntity.getStartAny(FPlayerColl.get());
	public static ArgReader<FPlayer> getStartAny() { return startAny; }
	
	private static ArgReader<FPlayer> fullOnline = ARSenderEntity.getFullOnline(FPlayerColl.get());
	public static ArgReader<FPlayer> getFullOnline() { return fullOnline; }
	
	private static ArgReader<FPlayer> startOnline = ARSenderEntity.getStartOnline(FPlayerColl.get());
	public static ArgReader<FPlayer> getStartOnline() { return startOnline; }
	
}
