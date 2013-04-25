package com.massivecraft.factions.chat.tag;

import com.massivecraft.factions.chat.ChatTagAbstract;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UConf;
import com.massivecraft.factions.entity.UPlayer;

public class ChatTagNameforce extends ChatTagAbstract
{
	// -------------------------------------------- //
	// INSTANCE & CONSTRUCT
	// -------------------------------------------- //
	
	private ChatTagNameforce() { super("factions_nameforce"); }
	private static ChatTagNameforce i = new ChatTagNameforce();
	public static ChatTagNameforce get() { return i; }
	
	// -------------------------------------------- //
	// OVERRIDE
	// -------------------------------------------- //

	@Override
	public String getReplacement(UPlayer fsender, UPlayer frecipient)
	{
		if (!UConf.get(fsender).enabled) return "";
		Faction faction = fsender.getFaction();
		return faction.getName();
	}

}
