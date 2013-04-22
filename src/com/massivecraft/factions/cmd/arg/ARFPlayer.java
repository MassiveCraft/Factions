package com.massivecraft.factions.cmd.arg;

import com.massivecraft.factions.entity.FPlayer;
import com.massivecraft.factions.entity.FPlayerColls;
import com.massivecraft.mcore.cmd.arg.ARSenderEntity;
import com.massivecraft.mcore.cmd.arg.ArgReader;

public class ARFPlayer
{
	// -------------------------------------------- //
	// INSTANCE
	// -------------------------------------------- //
	
	public static ArgReader<FPlayer> getFullAny(Object o) { return ARSenderEntity.getFullAny(FPlayerColls.get().get(o)); }
	
	public static ArgReader<FPlayer> getStartAny(Object o) { return ARSenderEntity.getStartAny(FPlayerColls.get().get(o)); }
	
	public static ArgReader<FPlayer> getFullOnline(Object o) { return ARSenderEntity.getFullOnline(FPlayerColls.get().get(o)); }
	
	public static ArgReader<FPlayer> getStartOnline(Object o) { return ARSenderEntity.getStartOnline(FPlayerColls.get().get(o)); }
	
}
